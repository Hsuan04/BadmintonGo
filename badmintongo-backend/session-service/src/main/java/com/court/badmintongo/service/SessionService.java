package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.SessionInfoPo;
import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import com.court.badmintongo.mapper.SessionMapper;
import com.court.badmintongo.repository.SessionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository SessionRepository;
    private final SessionMapper SessionMapper;

    @Transactional
    public SessionRs create(CreateSessionRq rq) {
        // 1. Rq 轉 PO (利用 MapStruct 處理時間格式)
        SessionInfoPo po = SessionMapper.toPo(rq);

        // 2. 初始化系統欄位
        po.setStatus(1); // 預設: 開放報名
        po.setCurrentParticipants(0);
        po.setWaitlistCount(0);
        po.setCreatedAt(OffsetDateTime.now());

        // 3. 儲存並轉回 Rs
        return SessionMapper.toRs(SessionRepository.save(po));
    }

    @Transactional
    public SessionRs update(UpdateSessionRq rq) {
        // 1. 查找現有資料
        SessionInfoPo po = SessionRepository.findById(rq.getPickupId())
                .orElseThrow(() -> new RuntimeException("臨打場次不存在"));

        // 2. 局部更新 (MapStruct 會自動忽略 Rq 裡的 null 欄位)
        SessionMapper.updatePoFromRq(rq, po);
        po.setUpdatedAt(OffsetDateTime.now());

        return SessionMapper.toRs(SessionRepository.save(po));
    }

    @Transactional
    public SessionRs softDelete(Long id) {
        SessionInfoPo po = SessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("臨打場次不存在"));

        po.setStatus(4);
        po.setUpdatedAt(OffsetDateTime.now());

        return SessionMapper.toRs(SessionRepository.save(po));
    }

    public Page<SessionRs> search(Map<String, String> params, Pageable pageable) {
        Specification<SessionInfoPo> spec = buildSpecification(params);
        return SessionRepository.findAll(spec, pageable).map(SessionMapper::toRs);
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
