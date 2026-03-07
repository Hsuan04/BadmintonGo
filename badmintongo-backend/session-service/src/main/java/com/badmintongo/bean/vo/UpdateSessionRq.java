package com.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 更新臨打場次請求參數
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新臨打場次請求參數")
public class UpdateSessionRq {

    @Schema(description = "臨打資料 ID", example = "100")
    @NotNull(message = "更新時 ID 不能為空") // 唯一必須傳入的欄位
    private Long pickupId;

    @Schema(description = "臨打日期 (yyyy-MM-dd)", example = "2026-03-15")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式必須為 yyyy-MM-dd")
    private String sessionDate;

    @Schema(description = "開始時間 (HH:mm:ss)", example = "19:00:00")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
    private String startTime;

    @Schema(description = "結束時間 (HH:mm:ss)", example = "21:00:00")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
    private String endTime;

    @Schema(description = "開放臨打人數", example = "10")
    @Min(value = 1, message = "開放人數至少 1 人")
    private Integer maxParticipants;

    @Schema(description = "臨打狀態 (1:開放報名, 2:已額滿, 3:已結束, 4:已取消)", example = "1")
    private Integer status;

    @Schema(description = "臨打說明", example = "更正：現場提供飲水機，但不提供球拍租借")
    private String description;

    @Schema(description = "最低程度要求 (1-5)", example = "3")
    @Range(min = 1, max = 5, message = "程度必須在 1-5 之間")
    private Integer minLevel;

    @Schema(description = "最高程度要求 (1-5)", example = "5")
    @Range(min = 1, max = 5, message = "程度必須在 1-5 之間")
    private Integer maxLevel;

    @Schema(description = "臨打使用用球", example = "RSL No.4")
    private String shuttlecockUsed;

    @Schema(description = "臨打負責人", example = "Lawrence")
    private String organizer;
}
