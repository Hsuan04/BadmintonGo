package com.badmintongo.bean.vo;

import com.badmintongo.bean.po.CourtInfoPo;
import com.badmintongo.bean.po.CourtOpenInfoPo;

import java.time.OffsetDateTime;
import java.util.List;

public record CourtRs(
        Integer courtId,
        String name,
        String category,
        Integer sportType,
        String address,
        String description,
        List<String> imageUrls,        // 查詢用，新增/刪除時可為空
        Integer status,
        OffsetDateTime createdAt,
        List<OpenTimeRs> openTimes     // 統一命名
) {
    public record OpenTimeRs(
            Integer dayOfWeek,
            Boolean isOpen,
            String openTime,
            String closeTime
    ) {}

    /**
     * 靜態工廠方法：將多個 PO 轉換為通用的 CourtRs
     */
    public static CourtRs from(CourtInfoPo po, List<CourtOpenInfoPo> openPos, List<String> imageUrls) {
        return new CourtRs(
                po.getCourtId(),
                po.getName(),
                po.getCategory(),
                po.getSportType(),
                po.getAddress(),
                po.getDescription(),
                imageUrls != null ? imageUrls : List.of(),
                po.getStatus(),
                po.getCreatedAt(),
                openPos == null ? List.of() : openPos.stream().map(o ->
                        new OpenTimeRs(
                                o.getDayOfWeek(),
                                o.getIsOpen(),
                                o.getOpenTime() != null ? o.getOpenTime().toString() : null,
                                o.getCloseTime() != null ? o.getCloseTime().toString() : null
                        )
                ).toList()
        );
    }
}
