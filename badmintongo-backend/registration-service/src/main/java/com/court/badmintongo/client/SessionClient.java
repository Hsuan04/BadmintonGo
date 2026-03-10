package com.court.badmintongo.client;

import com.court.badmintongo.result.Result;
import com.court.badmintongo.bean.vo.SessionInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(name = "session-service", url = "${app.services.session.url:http://localhost:8083}")
public interface SessionClient {

    @GetMapping("/api/sessions/{id}")
    SessionInfoVo getSessionBySessionId(@PathVariable("id") String id);

    @GetMapping("/api/sessions/internal")
    Result<Map<String, SessionInfoVo>> findSessionMapByCriteria(
            @RequestParam("courtName") String courtName,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("sessionStatus") Integer sessionStatus);

    @PostMapping("/api/sessions/internal/batch")
    Result<Map<String, SessionInfoVo>> findSessionMapByIds(@RequestBody List<String> ids);
}
