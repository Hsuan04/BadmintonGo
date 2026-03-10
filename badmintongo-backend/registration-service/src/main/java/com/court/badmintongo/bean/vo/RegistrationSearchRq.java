package com.court.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationSearchRq {
    // 身分標識
    private String userType;      // ADMIN, MEMBER, GUEST
    private String userId;        // 會員/訪客 ID (UUID)

    @Schema(description = "報名狀態: 0:尚未開始, 1:已結束, 2:已取消")
    private Integer registerStatus;

    // 管理者專用條件
    private String userName;      // 報名者姓名
    private String courtName;     // 場地名稱
    private LocalDate startDate;  // 場次日期區間-起
    private LocalDate endDate;    // 場次日期區間-迄
    private Integer sessionStatus; //場次狀態
}
