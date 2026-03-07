package com.court.badmintongo.mapper;

import com.court.badmintongo.bean.po.SessionInfoPo;
import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    SessionInfoPo toPo(CreateSessionRq rq);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sessionId", ignore = true)    // 主鍵不允許透過映射修改
    @Mapping(target = "sessionDate", ignore = true)  // 日期不允許修改
    @Mapping(target = "createdAt", ignore = true)    // 建立時間不允許修改
    void updatePoFromRq(UpdateSessionRq rq, @MappingTarget SessionInfoPo po);

    SessionRs toRs(SessionInfoPo po);
}
