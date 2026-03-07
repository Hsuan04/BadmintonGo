package com.court.badmintongo.mapper;

import com.court.badmintongo.bean.po.RegistrationInfoPo;
import com.court.badmintongo.bean.vo.CreateRegistrationRq;
import com.court.badmintongo.bean.vo.RegistrationRs;
import com.court.badmintongo.bean.vo.UpdateRegistrationRq;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegistrationMapper {

    // 1. 新增報名
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    RegistrationInfoPo toPo(CreateRegistrationRq rq);

    // 2. 更新報名
    void updatePoFromRq(UpdateRegistrationRq rq, @MappingTarget RegistrationInfoPo po);

    // 3. 輸出回應：因為型態統一，不再需要 qualifiedByName
    @Mapping(target = "registrationTime", source = "createdAt")
    RegistrationRs toRs(RegistrationInfoPo po);

    // 移除舊有的 toOffsetDateTime 方法
}
