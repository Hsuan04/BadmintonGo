package com.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 新增臨打場次請求參數
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新增臨打場次請求參數")
public class CreateSessionRq {


    @Schema(description = "關聯場地 ID", example = "1")
    @NotNull(message = "場地 ID 不能為空")
    private Integer courtId;

    @Schema(description = "臨打日期 (格式: yyyy-MM-dd)", example = "2026-03-10")
    @NotBlank(message = "臨打日期不能為空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式必須為 yyyy-MM-dd")
    private String sessionDate;

    @Schema(description = "開始時間 (格式: HH:mm:ss)", example = "18:00:00")
    @NotBlank(message = "開始時間不能為空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
    private String startTime;

    @Schema(description = "結束時間 (格式: HH:mm:ss)", example = "20:00:00")
    @NotBlank(message = "結束時間不能為空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
    private String endTime;

    @Schema(description = "開放臨打人數", example = "20")
    @NotNull(message = "開放人數不能為空")
    @Min(value = 1, message = "開放人數至少 1 人")
    private Integer maxParticipants;

    @Schema(description = "臨打說明", example = "新手友善場，有專業教練排點")
    private String description;

    @Schema(description = "最低程度要求 (1 ~ 18)", example = "4")
    @NotNull(message = "最低程度要求不能為空")
    @Range(min = 1, max = 18, message = "程度必須在 1-18 之間")
    private Integer minLevel;

    @Schema(description = "最低程度要求 (1 ~ 18)", example = "7")
    @NotNull(message = "最低程度要求不能為空")
    @Range(min = 1, max = 18, message = "程度必須在 1-18 之間")
    private Integer maxLevel;

    @Schema(description = "臨打使用用球", example = "勝利比賽級 (藍筒)")
    private String shuttlecockUsed;

    @Schema(description = "臨打負責人名稱", example = "Lawrence")
    @NotBlank(message = "負責人不能為空")
    private String organizer;
}
