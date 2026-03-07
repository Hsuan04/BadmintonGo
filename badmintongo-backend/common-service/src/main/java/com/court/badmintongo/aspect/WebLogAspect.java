package com.court.badmintongo.aspect;

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
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @Pointcut("execution(* com.court.badmintongo.controller..*.*(..))")
//    public void webLog() {}

//    @Around("webLog()")
//    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//        // 執行業務邏輯
//        Object result = joinPoint.proceed();
//
//        long executionTime = System.currentTimeMillis() - startTime;
//
//        Map<String, Object> logMap = new LinkedHashMap<>();
//        logMap.put("method", request.getMethod());
//        logMap.put("url", request.getRequestURL().toString());
//        logMap.put("class", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        logMap.put("args", joinPoint.getArgs());
//        logMap.put("response", result);
//        logMap.put("time_ms", executionTime);
//        logMap.put("client_ip", request.getRemoteAddr());
//
//        String jsonLog = objectMapper.writeValueAsString(logMap);
//        log.info("API_LOG: {}", jsonLog);
//
//        return result;
//    }
}
