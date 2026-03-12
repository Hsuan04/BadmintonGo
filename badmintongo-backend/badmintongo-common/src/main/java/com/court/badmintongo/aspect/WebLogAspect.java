package com.court.badmintongo.aspect;

import com.court.badmintongo.utils.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(com.court.badmintongo..*) && @within(org.springframework.web.bind.annotation.RestController)")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 1. 獲取請求資訊 (Request)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 顯示：進入 Controller 前的 Log
        log.info(">>>> [API 開始] URL: {} {}, 參數: {}", request.getMethod(), request.getRequestURL().toString(), JsonMapper.toJSON(joinPoint.getArgs()));

        // 2. 執行原方法
        Object result = joinPoint.proceed();

        // 3. 獲取回傳資訊 (Response)
        long timeMs = System.currentTimeMillis() - startTime;

        // 顯示：回傳前端前的 Log
        log.info("<<<< [API 結束] 耗時: {}ms, 回傳內容: {}", timeMs, JsonMapper.toJSON(result));

        return result;
    }
}
