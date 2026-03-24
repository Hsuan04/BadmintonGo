package com.court.badmintongo.service;


import com.court.badmintongo.constant.SystemEnum.UserType;
import com.court.badmintongo.result.Result;
import com.court.badmintongo.utils.JsonMapper;
import com.court.badmintongo.utils.RedisKeyUtils;
import com.court.badmintongo.bean.constants.RegistrationInfoMeta;
import com.court.badmintongo.bean.po.RegistrationInfoPo;
import com.court.badmintongo.bean.vo.*;
import com.court.badmintongo.client.SessionClient;
import com.court.badmintongo.mapper.RegistrationMapper;
import com.court.badmintongo.repository.RegistrationRepository;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private static final String SESSION_KEY_PREFIX = "badmintongo:session:%s:%s";
    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;
    private final SessionClient sessionClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public RegistrationRs create(CreateRegistrationRq rq) {
        // 1. 檢查該場次是否重複報名 (根據 sessionId + contactInfo)
//        if (registrationRepository.existsBySessionIdAndContactInfo(rq.getSessionId(), rq.getContactInfo())) {
//            throw new RuntimeException("該聯絡資訊已報名過此場次");
//        }

        // 2. 處理身分識別 (userId)
        String finalUserId;
        if (UserType.MEMBER.getCode().equals(rq.getUserType())) {
            finalUserId = rq.getUserId();
        } else {
            finalUserId = generateUniqueGuestId();
        }

        // 3. 轉換 PO 並設置關鍵欄位
        RegistrationInfoPo po = registrationMapper.toPo(rq);
        po.setUserId(finalUserId); // 不論身分，統一存入 userId 欄位
        String memberId = finalUserId; // Redis 使用這個統一的 ID

        // 4. Redis 排名邏輯 (維持不變)
        String metaKey = RedisKeyUtils.getSessionMetaKey(po.getSessionId());
        String poolKey = RedisKeyUtils.getSessionPoolKey(po.getSessionId());

        Object maxObj = redisTemplate.opsForHash().get(metaKey, "maxParticipants");
        if (maxObj == null) throw new RuntimeException("場次資訊不存在或已過期");
        int maxParticipants = Integer.parseInt(maxObj.toString());

        // 加入 Pool 並獲取排名
        redisTemplate.opsForZSet().add(poolKey, memberId, (double) System.currentTimeMillis());
        Long rank = redisTemplate.opsForZSet().rank(poolKey, memberId);

        if (rank == null) throw new RuntimeException("報名系統忙碌中");

        // 判定正備取狀態
        if (rank < maxParticipants) {
            po.setStatus(1);        // 正取
            po.setQueueOrder(0);
        } else {
            po.setStatus(0);        // 備取
            po.setQueueOrder(Math.toIntExact(rank - maxParticipants + 1));
        }

        po.setCreatedAt(LocalDateTime.now());

        // 存入 DB 並串接 Session 詳情回傳
        RegistrationInfoPo savedPo = registrationRepository.save(po);

        return RegistrationRs.from(savedPo);
    }

    /**
     * 產生唯一的 Guest UUID 並檢查資料庫衝突
     */
    private String generateUniqueGuestId() {
        String uuid;
        boolean exists;
        do {
            uuid = "G-" + UUID.randomUUID().toString().substring(0, 13); // 增加字首辨識，如 G-xxxx
            exists = registrationRepository.existsByUserId(uuid);        // 需在 Repo 補上此方法
        } while (exists);
        return uuid;
    }

    @Transactional
    public RegistrationRs update(UpdateRegistrationRq rq) {
        // 1. 查找現有紀錄
        RegistrationInfoPo po = registrationRepository.findById(rq.getId())
                .orElseThrow(() -> new RuntimeException("找不到該報名紀錄，ID: " + rq.getId()));

        // 2. 權限檢查：非管理員只能修改自己的資料
        if (!UserType.ADMIN.getCode().equals(rq.getUserType()) && !po.getUserId().equals(rq.getUserId())) {
            throw new RuntimeException("權限不足，無法修改他人報名資訊");
        }

        // 3. 狀態檢查：已取消的紀錄不可修改
        if (po.getStatus() != null && po.getStatus() == 2) {
            throw new RuntimeException("報名已取消，無法修改資訊");
        }

        // 4. 更新允許修改的欄位 (姓名、性別、程度、聯絡方式)
        if (rq.getUserName() != null) po.setUserName(rq.getUserName());
        if (rq.getSkillLevel() != null) po.setSkillLevel(rq.getSkillLevel());
        if (rq.getContactType() != null) po.setContactType(rq.getContactType());
        if (rq.getContactInfo() != null) po.setContactInfo(rq.getContactInfo());
        if (rq.getGender() != null) po.setGender(rq.getGender());

        // 5. 同步 Redis 排名資訊：確保回傳的 Rs 包含最新的正備取狀態
        updateStatusFromRedis(po);

        // 6. 存入資料庫
        RegistrationInfoPo savedPo = registrationRepository.save(po);

        // 7. 跨服務獲取場次詳情並回傳
        SessionInfoVo session = sessionClient.getSessionBySessionId(po.getSessionId());
        return RegistrationRs.from(savedPo, session);
    }

    /**
     * 輔助方法：根據 Redis 的 ZSet 排名重新設定 PO 的狀態
     */
    private void updateStatusFromRedis(RegistrationInfoPo po) {
        String metaKey = RedisKeyUtils.getSessionMetaKey(po.getSessionId());
        String poolKey = RedisKeyUtils.getSessionPoolKey(po.getSessionId());

        // 從 Redis 獲取該場次的最大人數上限
        Object maxObj = redisTemplate.opsForHash().get(metaKey, "maxParticipants");
        if (maxObj == null) return;
        int maxParticipants = Integer.parseInt(maxObj.toString());

        // 從 Redis 獲取該用戶目前的排名 (0-based)
        Long rank = redisTemplate.opsForZSet().rank(poolKey, po.getUserId());

        if (rank != null) {
            if (rank < maxParticipants) {
                po.setStatus(1); // 正取
                po.setQueueOrder(0);
            } else {
                po.setStatus(0); // 備取
                po.setQueueOrder(Math.toIntExact(rank - maxParticipants + 1));
            }
        }
    }

    /**
     * 分頁查詢報名紀錄 (整合身分權限與跨服務場次過濾)
     * @param rq 查詢請求 VO (包含 userType, userId, viewMode 等)
     * @param pageable 分頁參數
     * @return 補全場次資訊的報名紀錄分頁
     */
    public Page<RegistrationRs> search(RegistrationSearchRq rq, Pageable pageable) {
        // 1. 宣告一個處理用的臨時變數
        Map<String, SessionInfoVo> sessionMap = null;

        // --- 邏輯 B：跨服務過濾 SessionIds ---
        boolean needsSessionFilter = StringUtils.hasText(rq.getCourtName())
                || rq.getStartDate() != null
                || rq.getEndDate() != null
                || (rq.getSessionStatus() != null );

        if (needsSessionFilter) {
            Result<Map<String, SessionInfoVo>> result = sessionClient.findSessionMapByCriteria(
                    rq.getCourtName(), rq.getStartDate(), rq.getEndDate(), rq.getSessionStatus());
            log.info("==> 場次資料：{}", JsonMapper.toJSON(result));
            if (result != null && result.getData() != null) {
                sessionMap = result.getData();
            }

            if (CollectionUtils.isEmpty(sessionMap)) {
                return Page.empty(pageable);
            }
        }

        final Map<String, SessionInfoVo> finalSessionMap = sessionMap;

        // --- 邏輯 C：構建動態查詢 (Specification) ---
        Specification<RegistrationInfoPo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 1. 權限隔離：非管理員只能查自己的資料
            if (!UserType.ADMIN.getCode().equals(rq.getUserType())) {
                predicates.add(cb.equal(root.get(RegistrationInfoMeta.USER_ID), rq.getUserId()));
            }
            // 2. 狀態篩選 (使用新定義的 RegisterStatus Enum)
            if (rq.getRegisterStatus() != null) {
                Integer status = rq.getRegisterStatus();
                predicates.add(cb.equal(root.get(RegistrationInfoMeta.STATUS), status));
            }
            // 3. 場次 ID 聯動：帶入 SQL 的 IN 條件
            if (finalSessionMap != null && !finalSessionMap.isEmpty()) {
                predicates.add(root.get(RegistrationInfoMeta.SESSION_ID).in(finalSessionMap.keySet()));
            }
            // 4. 管理員模糊查詢報名者姓名
            if (UserType.ADMIN.getCode().equals(rq.getUserType()) && StringUtils.hasText(rq.getUserName())) {
                predicates.add(cb.like(root.get(RegistrationInfoMeta.USER_NAME), "%" + rq.getUserName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // --- 邏輯 D：執行查詢並補完顯示資料 ---
        Page<RegistrationInfoPo> poPage = registrationRepository.findAll(spec, pageable);
        log.info("==> [DB 查詢結束] 找到 {} 筆報名紀錄", poPage.getTotalElements());

        // 準備最終顯示用的 Lookup Map
        final Map<String, SessionInfoVo> fullLookupMap = new HashMap<>();

        if (finalSessionMap != null) {
            fullLookupMap.putAll(finalSessionMap);
        } else if (!poPage.isEmpty()) {
            List<String> idsOnPage = poPage.getContent().stream()
                    .map(RegistrationInfoPo::getSessionId).distinct().collect(Collectors.toList());

            Result<Map<String, SessionInfoVo>> batchResult = sessionClient.findSessionMapByIds(idsOnPage);

            if (batchResult != null && batchResult.getData() != null) {
                fullLookupMap.putAll(batchResult.getData());
            }
        }

        // 透過 Page.map 直接轉換為前端需要的 Response (RegistrationRs)
        return poPage.map(po -> {
            SessionInfoVo sessionVo = fullLookupMap.get(po.getSessionId());

            if (sessionVo == null) {
                log.error("==> [資料異常] 報名紀錄找不到對應的場次詳情 ID: {}", po.getSessionId());
            }

            return RegistrationRs.from(po, sessionVo);
        });
    }

    @Transactional
    public RegistrationRs delete(String id) {
        // 1. 先從 DB 找出資料
        RegistrationInfoPo po = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該報名紀錄"));

        // 2. 準備 Redis 資訊
        String poolKey = String.format(SESSION_KEY_PREFIX, po.getSessionId(), "pool");

        // 根據你當初存入 ZSet 的邏輯重建 memberId
        String memberId = UserType.MEMBER.getCode().equals(po.getUserType())
                ? String.valueOf(po.getUserId())
                : UserType.GUEST.getCode().concat(":").concat(po.getContactInfo());

        // 3. 執行 Redis 刪除 (ZREM)
        redisTemplate.opsForZSet().remove(poolKey, memberId);

        // 4. 執行 DB 刪除
        registrationRepository.delete(po);

        // 5. 回傳被刪除的資料詳情
        return registrationMapper.toRs(po);
    }

}
