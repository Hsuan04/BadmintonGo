package com.court.badmintongo.bean.vo;


import com.court.badmintongo.bean.po.RegistrationInfoPo;

import java.time.LocalDateTime;

/**
 * 報名臨打 Response
 */
public record RegistrationRs(
        // 報名紀錄本身
        Long registrationId,
        String status,
        Integer queueOrder,

        // 報名者資訊
        String name,
        Integer skillLevel,
        LocalDateTime registrationTime,

        SessionInfoVo pickupInfo
) {
    public static RegistrationRs from(RegistrationInfoPo regPo, SessionInfoVo pickupInfoVo) {
        return new RegistrationRs(
                regPo.getId(),
                regPo.getStatus(),
                regPo.getQueueOrder(),
                regPo.getName(),
                regPo.getSkillLevel(),
                regPo.getCreatedAt(),
                pickupInfoVo
        );
    }
}