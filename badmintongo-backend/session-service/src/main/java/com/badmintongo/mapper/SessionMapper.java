package com.badmintongo.mapper;

import com.badmintongo.bean.po.SessionInfoPo;
import com.badmintongo.bean.vo.CreateSessionRq;
import com.badmintongo.bean.vo.SessionRs;
import com.badmintongo.bean.vo.UpdateSessionRq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SessionMapper {

    // 將新增請求轉為 PO
    @Mapping(target = "startTime", source = "startTime", dateFormat = "HH:mm:ss")
    @Mapping(target = "endTime", source = "endTime", dateFormat = "HH:mm:ss")
    SessionInfoPo toPo(CreateSessionRq rq);

    // 局部更新：將 UpdateRq 的非 null 欄位更新到原本的 PO 上
    void updatePoFromRq(UpdateSessionRq rq, @MappingTarget SessionInfoPo po);

    // 將 PO 轉為 Rs 回傳
    @Mapping(target = "startTime", source = "startTime", dateFormat = "HH:mm:ss")
    @Mapping(target = "endTime", source = "endTime", dateFormat = "HH:mm:ss")
    SessionRs toRs(SessionInfoPo po);
}
