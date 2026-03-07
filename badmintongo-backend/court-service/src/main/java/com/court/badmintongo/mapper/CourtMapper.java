package com.court.badmintongo.mapper;

import com.court.badmintongo.bean.po.CourtInfoPo;
import com.court.badmintongo.bean.po.CourtOpenInfoPo;
import com.court.badmintongo.bean.vo.CourtRs;
import com.court.badmintongo.bean.vo.UpdateCourtRq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourtMapper {

    // --- 1. Request 轉 Entity (用於儲存) ---

    // 更新場地基本資訊 (排除 null)
    void updatePoFromRq(UpdateCourtRq rq, @MappingTarget CourtInfoPo po);

    // 開放時間：單筆轉換
    @Mapping(target = "openTime", source = "openTime", dateFormat = "HH:mm:ss")
    @Mapping(target = "closeTime", source = "closeTime", dateFormat = "HH:mm:ss")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courtId", ignore = true) // 建議也忽略 courtId，我們會在 Service 手動 set
    CourtOpenInfoPo toOpenPo(UpdateCourtRq.OpenTimeRq openRq);

    // 開放時間：列表轉換
    List<CourtOpenInfoPo> toOpenPoList(List<UpdateCourtRq.OpenTimeRq> openRqList);

    // --- 2. Entity 轉 Response (用於回傳) ---

    /**
     * 將多個來源組合成 CourtRs
     * target: CourtRs 裡的欄位名
     * source: 傳入此方法的參數名
     */
    @Mapping(target = "openTimes", source = "openTimeList") // 把參數 openTimeList 塞進 CourtRs 的 openTimes
    @Mapping(target = "imageUrls", source = "imageUrls")
    CourtRs toRs(CourtInfoPo po, List<CourtOpenInfoPo> openTimeList, List<String> imageUrls);

    // 開放時間：Entity 轉 Rs 用的內部物件
    @Mapping(target = "openTime", source = "openTime", dateFormat = "HH:mm:ss")
    @Mapping(target = "closeTime", source = "closeTime", dateFormat = "HH:mm:ss")
    CourtRs.OpenTimeRs toOpenRs(CourtOpenInfoPo openPo);
}
