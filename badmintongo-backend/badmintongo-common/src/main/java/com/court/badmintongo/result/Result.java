package com.court.badmintongo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    @Schema(description = "回應代碼", example = "200")
    private String code;

    @Schema(description = "回應訊息", example = "操作成功")
    private String message;

    @Schema(description = "回應數據內容")
    private T data;

    @Schema(description = "回應時間戳記", example = "1740493600000")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // 這裡就是你缺少的 success 方法！
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 失敗時用的方法
    public static <T> Result<T> error(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}