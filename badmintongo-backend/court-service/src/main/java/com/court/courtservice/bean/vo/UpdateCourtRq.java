package com.court.courtservice.bean.vo;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新球場請求參數")
public class UpdateCourtRq {

    @Schema(description = "場地名稱", example = "台大體育館-更新後名稱", maxLength = 100)
    @Size(max = 100, message = "場地名稱長度不能超過 100 字")
    private String name;

    @Schema(description = "場地類別 (例如：室內木板、室外水泥)", example = "室內木板")
    private String category;

    @Schema(description = "運動類型 (1:羽球, 2:籃球, 3:網球)", example = "1")
    @Min(value = 1, message = "不合法的運動類型")
    private Integer sportType;

    @Schema(description = "詳細地址", example = "台北市大安區羅斯福路四段1號")
    private String address;

    @Schema(description = "官方網站或預約連結 URL", example = "https://ntusportscenter.ntu.edu.tw/")
    private String url;

    @Schema(description = "場地詳細描述", example = "更新測試：冷氣維修完畢，全面開放。")
    private String description;

    @Schema(description = "場地狀態 (1:啟用, 2:停用, 4:已刪除)", example = "1")
    private Integer status;

    @Schema(description = "開放時段設定列表 (整組替換)")
    @Valid
    private List<OpenTimeRq> openTimeList;

    @Schema(description = "場地照片 S3 Key 列表", example = "[\"courts/main.jpg\", \"courts/side.jpg\"]")
    private List<String> imageKeys;

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

        @Schema(description = "開始營業時間 (HH:mm:ss)", example = "09:00:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
        private String openTime;

        @Schema(description = "結束營業時間 (HH:mm:ss)", example = "21:00:00")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "時間格式必須為 HH:mm:ss")
        private String closeTime;
    }
}