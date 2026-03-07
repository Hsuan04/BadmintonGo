package com.court.courtservice.bean.po;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * 基礎場地表 PO
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "court_info")
public class CourtInfoPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courtId;          // 場地 ID (BIGSERIAL)

    private String name;           // 場地名稱

    private String category;       // 分類：學校、私人場地、運動中心

    private Integer sportType;     // 運動類型 (SMALLINT)

    private String address;        // 地址

    private String url;            // 網址

    private String description;    // 場地描述

    private Integer status;        // 狀態：1: 審核中, 2: 開放, 3: 關閉

    private OffsetDateTime createdAt; // 建立時間 (TIMESTAMPTZ)

    private OffsetDateTime updatedAt; // 更新時間 (TIMESTAMPTZ)

}
