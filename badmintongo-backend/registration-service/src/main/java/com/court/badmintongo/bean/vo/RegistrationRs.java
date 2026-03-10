package com.court.badmintongo.bean.vo;


import com.court.badmintongo.bean.po.RegistrationInfoPo;

import java.time.LocalDateTime;

/**
 * 報名臨打 Response
 */
public record RegistrationRs(
        String registrationId,
        Integer status,
        String statusDesc,
        Integer queueOrder,
        String name,
        Integer skillLevel,
        LocalDateTime registrationTime,
        SessionInfoVo session // 第 8 個參數
) {
    /**
     * 手動新增一個構造函數，供 MapStruct 或舊邏輯使用
     */
    public RegistrationRs(String registrationId, Integer status, String statusDesc,
                          Integer queueOrder, String name, Integer skillLevel,
                          LocalDateTime registrationTime) {
        this(registrationId, status, statusDesc, queueOrder, name, skillLevel, registrationTime, null);
    }

    public static RegistrationRs from(RegistrationInfoPo regPo) {
        String desc = (regPo.getStatus() == 1) ? "正取成功" : "備取第 " + regPo.getQueueOrder() + " 位";
        return new RegistrationRs(
                regPo.getId(),
                regPo.getStatus(),
                desc,
                regPo.getQueueOrder(),
                regPo.getUserName(),
                regPo.getSkillLevel(),
                regPo.getCreatedAt()
        );
    }

    public static RegistrationRs from(RegistrationInfoPo regPo, SessionInfoVo session) {
        String desc = (regPo.getStatus() == 1) ? "正取成功" : "備取第 " + regPo.getQueueOrder() + " 位";
        return new RegistrationRs(
                regPo.getId(),
                regPo.getStatus(),
                desc,
                regPo.getQueueOrder(),
                regPo.getUserName(),
                regPo.getSkillLevel(),
                regPo.getCreatedAt(),
                session
        );
    }
}