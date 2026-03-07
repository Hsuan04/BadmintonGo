package com.badmintongo.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Builder
@Schema(description = "新增球場請求參數")
public class CreateCourtRq {

    @Schema(description = "場地名稱", example = "台大綜合體育館-羽球場A", maxLength = 100)
    @NotBlank(message = "場地名稱不能為空")
    @Size(max = 100, message = "場地名稱長度不能超過 100 字")
    private String name;

    @Schema(description = "場地類別", example = "室內木板")
    @NotBlank(message = "場地類別不能為空")
    private String category;

    @Schema(description = "運動類型 (1:羽球, 2:籃球, 3:網球)", example = "1")
    @NotNull(message = "運動類型不能為空")
    @Min(value = 1, message = "不合法的運動類型")
    private Integer sportType;

    @Schema(description = "詳細地址", example = "台北市大安區羅斯福路四段1號")
    @NotBlank(message = "地址不能為空")
    private String address;

    @Schema(description = "官方網站或預約連結 URL", example = "https://ntusportscenter.ntu.edu.tw/")
    private String url;

    @Schema(description = "場地詳細描述", example = "位於體育館三樓，光線充足，備有空調。")
    private String description;

    @Schema(description = "開放時段設定列表")
    @NotEmpty(message = "至少需設定一個開放時段")
    @Valid
    private List<OpenTimeRq> openTimeList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "開放時間設定")
    public static class OpenTimeRq {

        @Schema(description = "星期幾 (1:週一 ~ 7:週日)", example = "1")
        @NotNull(message = "星期幾不能為空")
        @Range(min = 1, max = 7, message = "星期必須在 1-7 之間")
        private Integer dayOfWeek;

        @Schema(description = "當日是否營業", example = "true")
        @NotNull(message = "是否開放標記不能為空")
        private Boolean isOpen;

        @Schema(description = "開始營業時間 (格式: HH:mm:ss)", example = "08:00:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
        private String openTime;

        @Schema(description = "結束營業時間 (格式: HH:mm:ss)", example = "22:00:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
        private String closeTime;
    }
}