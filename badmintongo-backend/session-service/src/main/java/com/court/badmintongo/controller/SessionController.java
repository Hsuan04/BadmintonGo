package com.court.badmintongo.controller;

import com.court.badmintongo.bean.vo.CreateSessionRq;
import com.court.badmintongo.bean.vo.SessionRs;
import com.court.badmintongo.bean.vo.UpdateSessionRq;
import com.court.courtservice.result.Result;
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

import java.util.Map;

@RestController
@RequestMapping("/api/Sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService SessionService;

    @Operation(summary = "建立臨打場次")
    @PostMapping
    public ResponseEntity<Result<SessionRs>> create(@Valid @RequestBody CreateSessionRq rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(SessionService.create(rq)));
    }

    @Operation(summary = "更新臨打場次")
    @PutMapping("/{id}")
    public ResponseEntity<Result<SessionRs>> update(@PathVariable Long id, @Valid @RequestBody UpdateSessionRq rq) {
        rq.setPickupId(id); // 確保 ID 以路徑為準
        return ResponseEntity.ok(Result.success(SessionService.update(rq)));
    }

    @Operation(summary = "刪除臨打場次 (邏輯刪除)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<SessionRs>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(Result.success(SessionService.softDelete(id)));
    }

    @Operation(summary = "分頁查詢臨打場次")
    @GetMapping
    public ResponseEntity<Result<Page<SessionRs>>> search(
            @RequestParam Map<String, String> params,
            @PageableDefault(sort = "sessionDate") Pageable pageable) {
        return ResponseEntity.ok(Result.success(SessionService.search(params, pageable)));
    }
}
