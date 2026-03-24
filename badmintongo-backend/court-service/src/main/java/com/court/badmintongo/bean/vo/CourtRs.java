package com.court.badmintongo.bean.vo;

import com.court.badmintongo.bean.po.CourtHolidayInfoPo;
import com.court.badmintongo.bean.po.CourtInfoPo;
import com.court.badmintongo.bean.po.CourtOpenInfoPo;
import com.court.badmintongo.constant.SystemEnum;

import java.time.OffsetDateTime;
import java.util.List;

public record CourtRs(
        String courtId,
        String name,
        String category,
        String categoryLabel,
        String sportType,
        String address,
        String description,
        String url,
        List<String> imageUrls,
        Integer status,
        String statusLabel,
        OffsetDateTime createdAt,
        List<OpenTimeRs> openTimes,
        List<HolidayRs> holidays
) {
    public record OpenTimeRs(
            Integer dayOfWeek,
            Boolean isOpen,
            String openTime,
            String closeTime
    ) {}
    public record HolidayRs(
            String date,
            String startTime,
            String endTime,
            String description
    ) {}

    /**
     * 靜態工廠方法：將多個 PO 轉換為通用的 CourtRs
     */
    public static CourtRs from(CourtInfoPo po, List<CourtOpenInfoPo> openPos, List<CourtHolidayInfoPo> holidayPos, List<String> imageUrls) {
        return new CourtRs(
                po.getCourtId(),
                po.getName(),
                po.getCategory(),
                SystemEnum.CourtCategory.getDescByCode(po.getCategory()),
                po.getSportType(),
                po.getAddress(),
                po.getDescription(),
                po.getUrl(),
                imageUrls != null ? imageUrls : List.of(),
                po.getStatus(),
                SystemEnum.CourtStatus.getDescByCode(po.getStatus()),
                po.getCreatedAt(),
                // 1. 轉換開放時間 (使用暗示解決推導問題)
                openPos == null ? List.<OpenTimeRs>of() : openPos.stream().map(o ->
                        new OpenTimeRs(
                                o.getDayOfWeek(),
                                o.getIsOpen(),
                                o.getOpenTime() != null ? o.getOpenTime().toString() : null,
                                o.getCloseTime() != null ? o.getCloseTime().toString() : null
                        )
                ).toList(),
                // 2. 轉換特殊休息日 (加上 List.<HolidayRs>of() 解決報錯)
                holidayPos == null ? List.<HolidayRs>of() : holidayPos.stream().map(h ->
                        new HolidayRs(
                                h.getHolidayDate().toString(),
                                h.getStartTime() != null ? h.getStartTime().toString() : null,
                                h.getEndTime() != null ? h.getEndTime().toString() : null,
                                h.getDescription()
                        )
                ).toList()
        );
    }
}
