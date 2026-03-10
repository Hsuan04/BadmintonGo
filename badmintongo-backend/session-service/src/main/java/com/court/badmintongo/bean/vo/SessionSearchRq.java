package com.court.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "場次查詢請求參數")
public class SessionSearchRq {

    @Schema(description = "場地名稱 (模糊查詢)", example = "小巨蛋")
    private String courtName;

    @Schema(description = "查詢起始日期", example = "2026-03-01")
    private LocalDate startDate;

    @Schema(description = "查詢結束日期", example = "2026-03-31")
    private LocalDate endDate;

    @Schema(description = "使用者程度 (會匹配在 minLevel 與 maxLevel 區間內的場次)", example = "5")
    private Integer userLevel;

    @Schema(description = "視圖模式 (0: 尚未開始, 1: 已結束, 3: 全部)", example = "0")
    private String viewMode;
}
