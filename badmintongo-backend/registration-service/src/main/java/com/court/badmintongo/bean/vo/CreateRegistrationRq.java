package com.court.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增臨打場次請求參數
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "玩家報名請求參數")
public class CreateRegistrationRq {

    @Schema(description = "場次 ID (對應特定日期的臨打時段)", example = "101")
    @NotNull(message = "場次 ID 不能為空")
    private Integer sessionId;

    @Schema(description = "報名者身分", example = "MEMBER", allowableValues = {"MEMBER", "GUEST"})
    @NotBlank(message = "身分類型不能為空")
    private String userType;

    @Schema(description = "會員 ID (若為訪客則帶 null)", example = "50")
    private Long userId;

    @Schema(description = "報名者姓名", example = "Lawrence")
    @NotBlank(message = "姓名不能為空")
    private String name;

    @Schema(description = "性別", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    @NotBlank(message = "性別不能為空")
    private String gender;

    @Schema(description = "報名者姓名", example = "5")
    @NotBlank(message = "程度分級不能為空")
    private Integer skillLevel;

    @Schema(description = "聯絡方式 (LINE ID 或手機)", example = "Line")
    @NotBlank(message = "聯絡方式不能為空")
    private String contactType;

    @Schema(description = "聯絡資訊", example = "line_id_123")
    @NotBlank(message = "聯絡資訊不能為空")
    private String contactInfo;
}