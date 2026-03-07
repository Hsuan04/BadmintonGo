package com.court.badmintongo.service;


import com.court.badmintongo.bean.po.RegistrationInfoPo;
import com.court.badmintongo.bean.vo.CreateRegistrationRq;
import com.court.badmintongo.bean.vo.SessionInfoVo;
import com.court.badmintongo.bean.vo.RegistrationRs;
import com.court.badmintongo.client.SessionClient;
import com.court.badmintongo.mapper.RegistrationMapper;
import com.court.badmintongo.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;
    private final SessionClient sessionClient;

    @Transactional
    public RegistrationRs create(CreateRegistrationRq rq) {
        // step.1 透由 session-service 取得場地資訊
        SessionInfoVo sessionVo = sessionClient.getSessionById(rq.getSessionId());

        // 2. Rq 轉 PO (MapStruct 已經在 toPo 裡處理了部分欄位)
        RegistrationInfoPo po = registrationMapper.toPo(rq);

        // 3. 判斷正取或候補邏輯
        // 這裡狀態判斷建議對齊 session-service 的業務邏輯
//        boolean hasSpace = sessionVo.getCurrentParticipants() < sessionVo.getMaxParticipants();
//
//        // 假設 sessionVo.getStatus() == 1 代表「開放報名」
//        if (sessionVo.getStatus() == 1 && hasSpace) {
//            po.setStatus("SUCCESS");
//            po.setQueueOrder(0);
//        } else {
//            po.setStatus("WAITING");
//            // 取得當前候補序號 (建議在 repository 實作這個方法)
//            int currentWaiting = registrationRepository.countBySessionIdAndStatus(rq.getSessionId(), "WAITING");
//            po.setQueueOrder(currentWaiting + 1);
//        }

        // 4. 系統時間欄位 (MapStruct 的 toPo 其實已經寫了 LocalDateTime.now())
        // 如果想確保儲存時間與判斷邏輯一致，保留手動 set 也可以
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now()); // 建議也補上更新時間

        // 5. 儲存並轉回 Rs
        RegistrationInfoPo savedPo = registrationRepository.save(po);
        return registrationMapper.toRs(savedPo);
    }
//
//    @Transactional
//    public PickupRs update(UpdatePickupRq rq) {
//        // 1. 查找現有資料
//        PickupInfoPo po = pickupRepository.findById(rq.getPickupId())
//                .orElseThrow(() -> new RuntimeException("臨打場次不存在"));
//
//        // 2. 局部更新 (MapStruct 會自動忽略 Rq 裡的 null 欄位)
//        pickupMapper.updatePoFromRq(rq, po);
//        po.setUpdatedAt(OffsetDateTime.now());
//
//        return pickupMapper.toRs(pickupRepository.save(po));
//    }
//
//    @Transactional
//    public PickupRs softDelete(Long id) {
//        PickupInfoPo po = pickupRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("臨打場次不存在"));
//
//        po.setStatus(4);
//        po.setUpdatedAt(OffsetDateTime.now());
//
//        return pickupMapper.toRs(pickupRepository.save(po));
//    }
//
//    public Page<PickupRs> search(Map<String, String> params, Pageable pageable) {
//        Specification<PickupInfoPo> spec = buildSpecification(params);
//        return pickupRepository.findAll(spec, pageable).map(pickupMapper::toRs);
//    }
//
//    private Specification<PickupInfoPo> buildSpecification(Map<String, String> params) {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            // 1. 場地名稱篩選
//            if (params.get("courtName") != null && !params.get("courtName").isEmpty()) {
//                predicates.add(cb.like(root.get("courtName"), "%" + params.get("courtName") + "%"));
//            }
//
//            // 2. 日期區間篩選 (動態處理開始與結束)
//            String startStr = params.get("startDate");
//            String endStr = params.get("endDate");
//
//            if (startStr != null && !startStr.isEmpty() && endStr != null && !endStr.isEmpty()) {
//                // (1)查詢區間內 (BETWEEN)
//                predicates.add(cb.between(root.get("sessionDate"), LocalDate.parse(startStr), LocalDate.parse(endStr)));
//            } else if (startStr != null && !startStr.isEmpty()) {
//                // (2)開始日期之後
//                predicates.add(cb.greaterThanOrEqualTo(root.get("sessionDate"), LocalDate.parse(startStr)));
//            } else if (endStr != null && !endStr.isEmpty()) {
//                // (3)結束日期之前
//                predicates.add(cb.lessThanOrEqualTo(root.get("sessionDate"), LocalDate.parse(endStr)));
//            }
//
//            // 3. 程度篩選
//            if (params.get("userLevel") != null) {
//                Integer level = Integer.parseInt(params.get("userLevel"));
//                predicates.add(cb.between(cb.literal(level), root.get("minLevel"), root.get("maxLevel")));
//            }
//
//            // 4. 狀態篩選 (排除已刪除)
//            predicates.add(cb.notEqual(root.get("status"), 4));
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//    }
}
