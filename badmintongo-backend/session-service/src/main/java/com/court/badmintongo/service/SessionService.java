package com.court.badmintongo.service;

import com.court.badmintongo.bean.dto.SessionRedisMeta;
import com.court.badmintongo.bean.po.SessionInfoPo;
import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import com.court.badmintongo.mapper.SessionMapper;
import com.court.badmintongo.repository.SessionRepository;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String SESSION_KEY_PREFIX = "badmintongo:session:%s:%s";
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final RedisTemplate<String, String> redisTemplate;

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

        // 2. 封裝 Redis 專用資料結構
        SessionRedisMeta redisMeta = new SessionRedisMeta(
                po.getMaxParticipants(),
                po.getCurrentParticipants(),
                po.getStatus(),
                LocalDateTime.now().toString()
        );

        // set data for redis
        String metaKey = String.format(SESSION_KEY_PREFIX, sessionId, "meta");  // Key 格式: badmintongo:session:{sessionId}:meta
        String shadowKey = String.format(SESSION_KEY_PREFIX, sessionId, "shadow");
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("maxParticipants", String.valueOf(redisMeta.maxParticipants()));           //Redis設定session最大報名人數
        metaData.put("currentParticipants", String.valueOf(redisMeta.currentParticipants()));   //創建時，session 已經報名人數預設為0
        metaData.put("status", String.valueOf(redisMeta.status()));                             //session 狀態
        //todo test
        metaData.put("createdAt", redisMeta.createdAt());
        //save to redis
        redisTemplate.opsForHash().putAll(metaKey, metaData);
        redisTemplate.opsForHash().putAll(shadowKey, metaData);

        // 1. 取得場次日期當天的 23:59:59
        LocalDateTime endOfDay = po.getSessionDate().atTime(LocalTime.MAX);
        long secondsUntilMidnight = Duration.between(LocalDateTime.now(), endOfDay).getSeconds();  // 2. 計算從「現在」到「當天深夜」還有多少秒
        // 3. 設定 Redis TTL
//        if (secondsUntilMidnight > 0) {
//            // 讓這筆資料在今天過完後自動消失
//            redisTemplate.expire(metaKey, Duration.ofSeconds(secondsUntilMidnight));
//        } else {
//            // 如果現在已經是深夜（例如 23:59:59 以後才建立），設定 1 小時後過期作為保險
//            redisTemplate.expire(metaKey, Duration.ofHours(1));
//        }
        //todo test
        redisTemplate.expire(metaKey, Duration.ofSeconds(30));

        return sessionMapper.toRs(savedPo);
    }

    @Transactional
    public SessionRs update(UpdateSessionRq rq) {
        // 1. 查找現有場次
        SessionInfoPo po = sessionRepository.findById(rq.getSessionId())
                .orElseThrow(() -> new RuntimeException("找不到該場次，ID: " + rq.getSessionId()));

        // 2. 狀態檢查：已結束(4) 或 已取消(5) 則不允許修改
        if (po.getStatus() != null && po.getStatus() >= 4) {
            throw new RuntimeException("場次已結束或已取消，無法修改資料");
        }

        // 3. 日期一致性校驗 (雖然 Mapper 忽略了映射，但邏輯上仍需比對)
        if (rq.getSessionDate() != null && !po.getSessionDate().equals(rq.getSessionDate())) {
            throw new RuntimeException("不可修改場次日期，若需更改請刪除並重新建立");
        }

        // 4. 使用 MapStruct 執行屬性更新
        sessionMapper.updatePoFromRq(rq, po);

        // 手動設定更新時間
        po.setUpdatedAt(LocalDateTime.now());

        // 5. 存回 PostgreSQL
        sessionRepository.save(po);

        // 6. 同步更新 Redis
        String metaKey = String.format("badmintongo:session:%s:meta", po.getSessionId());

        // 更新 Redis 中的關鍵控管欄位(若狀態更改為"取消"，則刪除 redis 資料)
        if (po.getStatus() != null && po.getStatus() == 5) {
            redisTemplate.delete(metaKey);
        } else {
            if (rq.getMaxParticipants() != null) {
                redisTemplate.opsForHash().put(metaKey, "maxParticipants", String.valueOf(rq.getMaxParticipants()));
            }
        }

        return sessionMapper.toRs(po);
    }

    @Transactional
    public SessionRs delete(String id) {
        // 1. 查找現有場次，確保存在
        SessionInfoPo po = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("臨打場次不存在"));

        // 2. 從 PostgreSQL 執行真正的刪除 (Hard Delete)
        sessionRepository.delete(po);

        // 3. 從 Redis 執行真正的刪除
        String metaKey = String.format("badmintongo:session:%s:meta", id);
        redisTemplate.delete(metaKey);

        return sessionMapper.toRs(po);
    }

    public Page<SessionRs> search(Map<String, String> params, Pageable pageable) {
        Specification<SessionInfoPo> spec = buildSpecification(params);
        Page<SessionInfoPo> dbResult = sessionRepository.findAll(spec, pageable);

        return dbResult.map(po -> {
            // 先從 PO 轉成基礎的 Rs
            SessionRs rs = SessionRs.from(po);

            // 獲取該場次的 Redis Meta Key
            String metaKey = String.format("badmintongo:session:%s:meta", po.getSessionId());
            Map<Object, Object> meta = redisTemplate.opsForHash().entries(metaKey);

            // 如果 Redis 有資料，則使用 Redis 的即時數據覆蓋 Rs
            if (meta != null && !meta.isEmpty()) {
                return new SessionRs(
                        rs.sessionId(),
                        rs.courtId(),
                        rs.courtName(),
                        rs.sessionDate(),
                        rs.startTime(),
                        rs.endTime(),
                        rs.maxParticipants(),
                        Integer.parseInt((String) meta.getOrDefault("currentParticipants", "0")),
                        Integer.parseInt((String) meta.getOrDefault("waitlistCount", "0")),
                        Integer.parseInt((String) meta.getOrDefault("status", String.valueOf(rs.status()))),
                        rs.description(),
                        rs.minLevel(),
                        rs.maxLevel(),
                        rs.shuttlecockUsed(),
                        rs.organizer(),
                        rs.createdAt()
                );
            }
            return rs;
        });
    }

    private Specification<SessionInfoPo> buildSpecification(Map<String, String> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 場地名稱篩選
            if (params.get("courtName") != null && !params.get("courtName").isEmpty()) {
                predicates.add(cb.like(root.get("courtName"), "%" + params.get("courtName") + "%"));
            }

            // 2. 日期區間篩選 (動態處理開始與結束)
            String startStr = params.get("startDate");
            String endStr = params.get("endDate");

            if (startStr != null && !startStr.isEmpty() && endStr != null && !endStr.isEmpty()) {
                // (1)查詢區間內 (BETWEEN)
                predicates.add(cb.between(root.get("sessionDate"), LocalDate.parse(startStr), LocalDate.parse(endStr)));
            } else if (startStr != null && !startStr.isEmpty()) {
                // (2)開始日期之後
                predicates.add(cb.greaterThanOrEqualTo(root.get("sessionDate"), LocalDate.parse(startStr)));
            } else if (endStr != null && !endStr.isEmpty()) {
                // (3)結束日期之前
                predicates.add(cb.lessThanOrEqualTo(root.get("sessionDate"), LocalDate.parse(endStr)));
            }

            // 3. 程度篩選
            if (params.get("userLevel") != null) {
                Integer level = Integer.parseInt(params.get("userLevel"));
                predicates.add(cb.between(cb.literal(level), root.get("minLevel"), root.get("maxLevel")));
            }

            // 4. 狀態篩選 (排除已刪除)
            predicates.add(cb.notEqual(root.get("status"), 4));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
