package com.court.badmintongo.exception;

import com.court.badmintongo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 1. 處理業務邏輯異常 (RuntimeException)
     * 例如你在 Service 裡 throw new RuntimeException("場次已結束")
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        // 💡 搭配我們之前設的 Log 格式，這裡會印出帶有 TraceID 的錯誤堆疊
        log.error("[全域異常攔截] 發生業務錯誤: {}", e.getMessage(), e);
        return Result.error("500", e.getMessage());
    }

    /**
     * 2. 處理參數校驗異常 (@Valid 失敗)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("[全域異常攔截] 參數校驗失敗: {}", errorMsg);
        return Result.error("400", errorMsg);
    }

    /**
     * 3. 處理最底層的未知異常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[全域異常攔截] 未知錯誤, 發生位置: {}.{}() 第 {} 行",
                e.getStackTrace()[0].getClassName(),
                e.getStackTrace()[0].getMethodName(),
                e.getStackTrace()[0].getLineNumber(),
                e);
        return Result.error("500", "系統錯誤");
    }

    /**
     * 4. 處理各個service拋出自定義的 Exception
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("[業務異常] 錯誤代碼: {}, 原因: {}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        Map<String, Object> errorRs = new HashMap<>();
        errorRs.put("success", false);
        errorRs.put("message", "wrong account / email or password");
        errorRs.put("code", 401);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRs);
    }
}
