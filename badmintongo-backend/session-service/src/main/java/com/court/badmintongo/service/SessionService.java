package com.court.badmintongo.service;

import com.court.badmintongo.enums.SessionReturnCode;
import com.court.badmintongo.exception.BusinessException;
import com.court.badmintongo.utils.JsonMapper;
import com.court.badmintongo.utils.RedisKeyUtils;
import com.court.badmintongo.bean.constants.SessionInfoMeta;
import com.court.badmintongo.bean.dto.SessionRedisMeta;
import com.court.badmintongo.bean.po.SessionInfoPo;
import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.SessionSearchRq;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import com.court.badmintongo.mapper.SessionMapper;
import com.court.badmintongo.repository.SessionRepository;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 建立新的臨打場次
     * 邏輯：存入 DB 後同步初始化 Redis Meta 與 Shadow Key，並根據場次日期設定過期時間。
     * @param rq 建立場次請求參數
     * @return 建立成功的場次資訊
     */
    @Transactional
    public SessionRs create(CreateSessionRq rq) {

        SessionInfoPo po = sessionMapper.toPo(rq);
        String sessionId = TSID.fast().toString();

        po.setSessionId(sessionId);
        po.setCreatedAt(LocalDateTime.now());
        po.setCurrentParticipants(0);
        po.setWaitlistCount(0);

        // save to DB
        SessionInfoPo savedPo = sessionRepository.save(po);
        log.info("==> [建立Session(場次資料)] DB 存檔成功，結果: {}", JsonMapper.toJSON(savedPo));

        // 2. 封裝 Redis 專用資料結構
        SessionRedisMeta redisMeta = new SessionRedisMeta(
                po.getMaxParticipants(),
                po.getCurrentParticipants(),
                po.getStatus(),
                LocalDateTime.now().toString()
        );

        // set data for redis
        String metaKey = RedisKeyUtils.getSessionMetaKey(sessionId);   // Key 格式: badmintongo:session:{sessionId}:meta
        String shadowKey = RedisKeyUtils.getSessionShadowKey(sessionId);
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("maxParticipants", String.valueOf(redisMeta.maxParticipants()));           //Redis設定session最大報名人數
        metaData.put("currentParticipants", String.valueOf(redisMeta.currentParticipants()));   //創建時，session 已經報名人數預設為0
        metaData.put("status", String.valueOf(redisMeta.status()));                             //session 狀態
        metaData.put("createdAt", redisMeta.createdAt());
        //save to redis
        redisTemplate.opsForHash().putAll(metaKey, metaData);
        redisTemplate.opsForHash().putAll(shadowKey, metaData);
        log.info("==> [建立Session(場次資料)] Redis 存檔成功，結果: {}", JsonMapper.toJSON(metaData));

        // 1. 取得場次日期當天的 23:59:59
        LocalDateTime endOfDay = po.getSessionDate().atTime(LocalTime.MAX);
        long secondsUntilMidnight = Duration.between(LocalDateTime.now(), endOfDay).getSeconds();  // 2. 計算從「現在」到「當天深夜」還有多少秒
        // 3. 設定 Redis TTL
        if (secondsUntilMidnight > 0) {
            // 讓這筆資料在今天過完後自動消失
            redisTemplate.expire(metaKey, Duration.ofSeconds(secondsUntilMidnight));
        } else {
            // 如果現在已經是深夜（例如 23:59:59 以後才建立），設定 1 小時後過期作為保險
            redisTemplate.expire(metaKey, Duration.ofHours(1));
        }

        return sessionMapper.toRs(savedPo);
    }

    /**
     * 更新場次資訊
     * 邏輯：包含狀態校驗（結束/取消不可改）與日期一致性檢查。
     * @param rq 更新參數
     * @return 更新後的場次資訊
     */
    @Transactional
    public SessionRs update(UpdateSessionRq rq) {
        // 1. 查找現有場次
        SessionInfoPo po = sessionRepository.findById(rq.getSessionId())
                .orElseThrow(() -> {
                    log.warn("==> [更新場次] 失敗: 找不到場次 ID: {}", rq.getSessionId());
                    return new RuntimeException("找不到該場次");
                });

        // 2. 狀態檢查：已結束(4) 或 已取消(5) 則不允許修改
        if (po.getStatus() != null && po.getStatus() >= 4) {
            throw new BusinessException(SessionReturnCode.SESSION_STATUS_LOCKED);
        }

        // 3. 日期一致性校驗 (雖然 Mapper 忽略了映射，但邏輯上仍需比對)
        if (rq.getSessionDate() != null && !po.getSessionDate().equals(rq.getSessionDate())) {
            throw new BusinessException(SessionReturnCode.DATE_CHANGE_NOT_ALLOWED);
        }

        // 4. 使用 MapStruct 執行屬性更新
        sessionMapper.updatePoFromRq(rq, po);

        // 手動設定更新時間
        po.setUpdatedAt(LocalDateTime.now());

        // 5. 存回 PostgreSQL
        log.info("==> 預計更新 DB 資料內容: {}", JsonMapper.toJSON(po));
        sessionRepository.save(po);
        log.info("==> [更新Session(場次資料)] DB 更新成功");

        // 6. 同步更新 Redis
        String metaKey = RedisKeyUtils.getSessionMetaKey(po.getSessionId());

        // 更新 Redis 中的關鍵控管欄位(若狀態更改為"取消"，則刪除 redis 資料)
        if (po.getStatus() != null && po.getStatus() == 5) {
            log.info("==> 因此資料更新為取消，預計刪除 Redis 資料內容, metaKey: {}", metaKey);
            redisTemplate.delete(metaKey);
        } else {
            if (rq.getMaxParticipants() != null) {
                log.info("==> 預計過新 Redis 資料內容, 報名人數上限: {}", rq.getMaxParticipants());
                redisTemplate.opsForHash().put(metaKey, "maxParticipants", String.valueOf(rq.getMaxParticipants()));
            }
        }

        return sessionMapper.toRs(po);
    }

    /**
     * 刪除場次 (Hard Delete)
     * 邏輯：同時從 DB 物理刪除並清理所有相關 Redis Keys (Meta, Pool, Shadow)。
     * @param id 場次 ID
     * @return 刪除前的場次資料備份
     */
    @Transactional
    public SessionRs delete(String id) {
        // 1. 查找現有場次，確保存在
        SessionInfoPo po = sessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("==> [刪除Session] 失敗: 找不到場次 ID: {}", id);
                    return new BusinessException(SessionReturnCode.SESSION_NOT_FOUND);
                });

        // 2. 從 PostgreSQL 執行真正的刪除 (Hard Delete)
        sessionRepository.delete(po);
        log.info("==> [刪除Session(場次資料)] DB 刪除成功");

        // 3. 從 Redis 執行真正的刪除
        String metaKey = RedisKeyUtils.getSessionMetaKey(id);
        log.info("==> 預計刪除 Redis meta key: {}", metaKey);
        redisTemplate.delete(metaKey);
        log.info("==> [刪除Session(場次資料)] Redis 刪除成功");

        return sessionMapper.toRs(po);
    }

    /**
     * 分頁查詢場次資訊
     * 邏輯：查詢 DB 後，透過 Redis 補齊即時報名人數與狀態，確保使用者看到最新名額。
     * @param sessionSearchRq 篩選條件 VO
     * @param pageable 分頁參數
     * @return 分頁結果
     */
    public Page<SessionRs> search(SessionSearchRq sessionSearchRq, Pageable pageable) {
        // 1. 使用強型別 Rq 建立 Specification
        Specification<SessionInfoPo> spec = buildSpecification(sessionSearchRq);
        Page<SessionInfoPo> dbResult = sessionRepository.findAll(spec, pageable);
        log.info("==> [查詢Session(場次資料)] DB 資料內容：{}",  JsonMapper.toJSON(dbResult));

        // 2. 映射結果並補充 Redis 即時資訊
        return dbResult.map(po -> {
            SessionRs rs = SessionRs.from(po);
            String metaKey = RedisKeyUtils.getSessionMetaKey(po.getSessionId());
            Map<Object, Object> meta = redisTemplate.opsForHash().entries(metaKey);

            if (meta != null && !meta.isEmpty()) {
                return rs.updateRealTimeData(meta);
            }
            return rs;
        });
    }

    /**
     * 內部方法：建立動態查詢條件 (Specification)
     * 使用 SessionInfoMeta 常數類別避免 Hardcode 字串，提高維護性。
     */
    private Specification<SessionInfoPo> buildSpecification(SessionSearchRq sessionSearchRq) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 場地名稱篩選
            if (StringUtils.hasText(sessionSearchRq.getCourtName())) {
                predicates.add(cb.like(root.get(SessionInfoMeta.COURT_NAME), "%" + sessionSearchRq.getCourtName() + "%"));
            }

            // 2. 日期區間篩選
            if (sessionSearchRq.getStartDate() != null && sessionSearchRq.getEndDate() != null) {
                predicates.add(cb.between(root.get(SessionInfoMeta.SESSION_DATE), sessionSearchRq.getStartDate(), sessionSearchRq.getEndDate()));
            } else if (sessionSearchRq.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(SessionInfoMeta.SESSION_DATE), sessionSearchRq.getStartDate()));
            } else if (sessionSearchRq.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(SessionInfoMeta.SESSION_DATE), sessionSearchRq.getEndDate()));
            }

            // 3. 程度篩選
            if (sessionSearchRq.getUserLevel() != null) {
                predicates.add(cb.between(cb.literal(sessionSearchRq.getUserLevel()),
                        root.get(SessionInfoMeta.MIN_LEVEL),
                        root.get(SessionInfoMeta.MAX_LEVEL)));
            }

            // 4. 狀態篩選
            predicates.add(cb.notEqual(root.get(SessionInfoMeta.STATUS), 4));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 根據 ID 取得單筆場次詳情
     * @param id 場次 ID
     * @return 場次詳情
     */
    public SessionRs getById(String id) {
        SessionInfoPo po = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該場次資料"));
        return sessionMapper.toRs(po);
    }

    /**
     * 跨服務專用：根據條件篩選場次 ID 列表
     * 邏輯：不處理分頁與 Redis 即時資訊，僅根據場地、時間與 viewMode 過濾 DB。
     *
     * @param courtName 場地名稱 (模糊查詢)
     * @param startDate 開始日期
     * @param endDate   結束日期
     * @param sessionStatus  場地狀態 (2:尚未開始, 3:已結束)
     * @return 符合條件的 sessionId 列表
     */
    public Map<String, SessionInfoPo> findSessionMapByCriteria(String courtName, LocalDate startDate, LocalDate endDate, Integer sessionStatus) {
        Specification<SessionInfoPo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 場地名稱篩選 (模糊查詢)
            if (StringUtils.hasText(courtName)) {
                predicates.add(cb.like(root.get(SessionInfoMeta.COURT_NAME), "%" + courtName + "%"));
            }

            // 2. 日期區間篩選
            // 如果只有 startDate -> 查 startDate 之後的所有資料
            if (startDate != null && endDate == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(SessionInfoMeta.SESSION_DATE), startDate));
            }
            // 如果只有 endDate -> 查 endDate 之前的所有資料
            else if (startDate == null && endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(SessionInfoMeta.SESSION_DATE), endDate));
            }
            // 如果兩者都有 -> 查區間
            else if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get(SessionInfoMeta.SESSION_DATE), startDate, endDate));
            }

            // 3. sessionStatus 篩選
            if (sessionStatus != null) {
                predicates.add(cb.equal(root.get("sessionStatus"), sessionStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 4. 執行查詢並回傳 ID 列表
        return sessionRepository.findAll(spec).stream()
                .collect(Collectors.toMap(
                        SessionInfoPo::getSessionId,
                        sessionPo -> sessionPo,
                        (existing, replacement) -> existing
                ));
    }

    public Map<String, SessionInfoPo> findSessionMapByIds(List<String> sessionIdList) {
        if (CollectionUtils.isEmpty(sessionIdList)) return new HashMap<>();

        // 1. 使用 JPA 的 IN 查詢：SELECT * FROM session_info WHERE session_id IN (...)
        List<SessionInfoPo> sessionPoList = sessionRepository.findBySessionIdIn(sessionIdList);

        // 2. 將 List 轉為 Map<String, Vo>，方便 registration-service 直接用 key 找資料
        return sessionPoList.stream().collect(Collectors.toMap(
                SessionInfoPo::getSessionId,
                po -> po,
                (existing, replacement) -> existing
        ));
    }

}
