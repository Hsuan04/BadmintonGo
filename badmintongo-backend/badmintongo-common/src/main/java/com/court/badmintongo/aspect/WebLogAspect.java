package com.court.badmintongo.aspect;

import com.court.badmintongo.utils.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 💡 1. 處理參數：過濾掉無法序列化的物件 (如 MultipartFile)
        Object[] args = joinPoint.getArgs();
        List<Object> logArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof MultipartFile || arg instanceof MultipartFile[] ||
                    arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                logArgs.add("[File or Servlet Object]"); // 檔案類型的參數用文字代替
            } else {
                logArgs.add(arg);
            }
        }

        // 顯示：參數使用過濾後的 logArgs
        log.info(">>>> [API 開始] URL: {} {}, 參數: {}",
                request.getMethod(),
                request.getRequestURL().toString(),
                JsonMapper.toJSON(logArgs));

        // 2. 執行原方法
        Object result = joinPoint.proceed();

        long timeMs = System.currentTimeMillis() - startTime;

        String resultJson = (result instanceof ResponseEntity) ?
                JsonMapper.toJSON(result) : String.valueOf(result);

        log.info("<<<< [API 結束] 耗時: {}ms, 回傳內容: {}", timeMs, resultJson);

        return result;
    }
}
