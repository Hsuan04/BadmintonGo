package com.court.badmintongo.client;

import com.court.badmintongo.bean.vo.SessionInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "session-service", url = "${app.services.session.url:http://localhost:8083}")
public interface SessionClient {

    @GetMapping("/api/v1/sessions/{id}")
    SessionInfoVo getSessionById(@PathVariable("id") Integer id);
}
