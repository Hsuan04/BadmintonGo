package com.court.badmintongo.controller;

import com.court.badmintongo.bean.po.SessionInfoPo;
import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.SessionSearchRq;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import com.court.badmintongo.result.Result;
import com.court.badmintongo.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "建立臨打場次")
    @PostMapping
    public ResponseEntity<Result<SessionRs>> create(@Valid @RequestBody CreateSessionRq rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(sessionService.create(rq)));
    }

    @Operation(summary = "更新臨打場次")
    @PutMapping("/{id}")
    public ResponseEntity<Result<SessionRs>> update(@PathVariable String id, @Valid @RequestBody UpdateSessionRq rq) {
        rq.setSessionId(id); // 確保 ID 以路徑為準
        return ResponseEntity.ok(Result.success(sessionService.update(rq)));
    }

    @Operation(summary = "刪除臨打場次")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<SessionRs>> delete(@PathVariable String id) {
        return ResponseEntity.ok(Result.success(sessionService.delete(id)));
    }

    @Operation(summary = "分頁查詢臨打場次")
    @GetMapping
    public ResponseEntity<Result<Page<SessionRs>>> search(
            SessionSearchRq sessionSearchRq,
            @PageableDefault(sort = "sessionDate") Pageable pageable) {
        return ResponseEntity.ok(Result.success(sessionService.search(sessionSearchRq, pageable)));
    }

    @Operation(summary = "取得單一臨打場次詳情")
    @GetMapping("/{id}")
    public Result<SessionRs> getById(@PathVariable String id) {
        return Result.success(sessionService.getById(id));
    }

    @Operation(summary = "提供給報名服務呼叫使用")
    @GetMapping("/internal") // 對應到 registration-service 中 SessionClient
    public Result<Map<String, SessionInfoPo>> findSessionMapByCriteria(
            @RequestParam(required = false) String courtName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer sessionStatus) {

        Map<String, SessionInfoPo> sessionMap = sessionService.findSessionMapByCriteria(courtName, startDate, endDate, sessionStatus);
        return Result.success(sessionMap);
    }

    @PostMapping("/internal/batch")
    public Result<Map<String, SessionInfoPo>> findSessionMapByIds(@RequestBody List<String> sessionIdList) {
        Map<String, SessionInfoPo> resultMap = sessionService.findSessionMapByIds(sessionIdList);
        return Result.success(resultMap);
    }

}
