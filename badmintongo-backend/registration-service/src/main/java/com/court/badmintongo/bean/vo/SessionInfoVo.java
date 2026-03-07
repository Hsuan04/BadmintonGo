package com.court.badmintongo.bean.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 供報名系統使用的場次資訊快照 (從 session-service 取得)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfoVo {

    private Long pickupId;            // 場次 ID，用於對應與驗證

    private String courtName;         // 直接提供場地名稱，報名系統不需再去查 court-service

    private LocalDate sessionDate;    // 臨打日期

    private LocalTime startTime;      // 開始時間

    private LocalTime endTime;        // 結束時間

    private String organizer;         // 負責人 (聯絡對象)

    private String description;       // 場次說明 (例如：幾號場、集合地點)

    private Integer status;           // 場次狀態 (判斷是否還能報名)


    private Integer minLevel;         // 最低級數
    private Integer maxLevel;         // 最高級數
}
