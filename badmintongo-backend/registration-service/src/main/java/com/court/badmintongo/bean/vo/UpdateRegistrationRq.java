package com.court.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 更新報名資訊請求參數
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新報名資訊請求參數")
public class UpdateRegistrationRq {

    @Schema(description = "報名紀錄流水號", example = "1024")
    @NotNull(message = "報名 ID 不能為空")
    private String id;

    @Schema(description = "報名者身分", example = "MEMBER", allowableValues = {"MEMBER", "GUEST"})
    @NotBlank(message = "身分類型不能為空")
    private String userType;

    @Schema(description = "報名者id", example = "G-1H6D8JA23CA35L")
    private String userId;

    @Schema(description = "報名者姓名", example = "Lawrence")
    private String userName;

    @Schema(description = "性別", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private String gender;

    @Schema(description = "羽球程度分級 (1-18)", example = "6")
    @Range(min = 1, max = 18, message = "程度必須在 1-18 之間")
    private Integer skillLevel;

    @Schema(description = "聯絡方式類型", example = "LINE")
    private String contactType;

    @Schema(description = "聯絡資訊", example = "new_line_id_456")
    private String contactInfo;

}