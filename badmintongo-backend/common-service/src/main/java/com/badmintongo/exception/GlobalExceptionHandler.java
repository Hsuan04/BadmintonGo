package com.badmintongo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 攔截 [註解驗證] 失敗 (格式錯誤)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // 呼叫統一格式方法
        return buildResponse("E001", "資料校驗失敗", errors);
    }

    /**
     * 2. 攔截 [業務邏輯] 失敗 (例如：Service 拋出的名稱重複)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        Map<String, String> errors = new HashMap<>();

        // 根據錯誤代碼，把錯誤對應到具體欄位上，讓前端好顯示
        if ("C101".equals(ex.getCode())) {
            errors.put("name", ex.getMessage());
        }

        return buildResponse(ex.getCode(), ex.getMessage(), errors);
    }

    /**
     * 統一格式工具方法
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String code, String msg, Map<String, String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", msg);
        body.put("details", (details != null && !details.isEmpty()) ? details : null);
        return ResponseEntity.badRequest().body(body);
    }
}
