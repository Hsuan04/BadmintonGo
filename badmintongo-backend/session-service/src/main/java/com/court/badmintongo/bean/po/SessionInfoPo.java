package com.court.badmintongo.bean.po;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * 臨打場次資訊表 PO
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_info")
public class SessionInfoPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pickupId;            // 臨打資料 ID (建議用 Long 對應 BIGSERIAL)

    @Column(nullable = false)
    private Integer courtId;          // 關聯場地 ID (對應 CourtInfoPo 的 courtId)

    private String courtName;         // 場地名稱

    private LocalDate sessionDate;    // 臨打日期

    private LocalTime startTime;      // 臨打開始時間

    private LocalTime endTime;        // 臨打結束時間

    private Integer maxParticipants;  // 開放臨打人數

    private Integer currentParticipants; // 已經報名人數

    private Integer waitlistCount;    // 候補人數

    private Integer status;           // 狀態：1: 開放報名, 2: 候補中, 3: 已額滿, 3: 已結束

    private String description;       // 臨打說明

    // --- 程度分級範圍 ---
    private Integer minLevel;         // 程度下限 (例如: 1:新手)
    private Integer maxLevel;         // 程度上限 (例如: 3:中下)

    private String shuttlecockUsed;   // 臨打使用用球

    private String organizer;         // 臨打負責人

    @Column(updatable = false)
    private OffsetDateTime createdAt; // 建立日期

    private OffsetDateTime updatedAt; // 更新日期
}
