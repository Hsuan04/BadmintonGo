package com.court.courtservice.bean.po;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * 場地開放時間表 PO
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "court_open_info")
public class CourtOpenInfoPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;            // ID (SERIAL)

    private Integer courtId;          // 關聯的場地 ID

    private Integer dayOfWeek;     // 1-7 代表週一至週日

    private Boolean isOpen;        // 是否營業

    private LocalTime openTime;    // 開始營業時間 (TIME)

    private LocalTime closeTime;   // 結束營業時間 (TIME)
}
