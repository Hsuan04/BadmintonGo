package com.court.badmintongo.bean.vo;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 供報名系統使用的場次資訊快照 (從 session-service 取得)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfoVo {

    private String sessionId;          // 臨打資料 ID
    private String courtId;          // 關聯場地 ID (對應 CourtInfoPo 的 courtId)
    private String courtName;         // 場地名稱
    private LocalDate sessionDate;    // 臨打日期
    private LocalTime startTime;      // 臨打開始時間
    private LocalTime endTime;        // 臨打結束時間
    private Integer maxParticipants;  // 開放臨打人數
    private Integer currentParticipants; // 已經報名人數
    private Integer waitlistCount;    // 候補人數
    private Integer status;           // 狀態：1: 開放報名, 2: 候補中, 3: 已額滿, 3: 已結束
    private String description;       // 臨打說明
    private Integer minLevel;         // 程度下限 (例如: 1:新手)
    private Integer maxLevel;         // 程度上限 (例如: 3:中下)
    private String shuttlecockUsed;   // 臨打使用用球
    private String organizer;         // 臨打負責人
    private LocalDateTime createdAt; // 建立日期
    private LocalDateTime updatedAt; // 更新日期
}
