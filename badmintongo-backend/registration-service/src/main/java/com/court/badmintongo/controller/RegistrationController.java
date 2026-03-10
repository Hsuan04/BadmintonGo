package com.court.badmintongo.controller;


import com.court.badmintongo.result.Result;
import com.court.badmintongo.bean.vo.CreateRegistrationRq;
import com.court.badmintongo.bean.vo.RegistrationRs;
import com.court.badmintongo.bean.vo.RegistrationSearchRq;
import com.court.badmintongo.bean.vo.UpdateRegistrationRq;
import com.court.badmintongo.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "建立報名資料")
    @PostMapping
    public ResponseEntity<Result<RegistrationRs>> create(@Valid @RequestBody CreateRegistrationRq rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(registrationService.create(rq)));
    }

    @Operation(summary = "更新報名資訊", description = "修改報名者的姓名、聯絡方式、程度")
    @PutMapping
    public ResponseEntity<Result<RegistrationRs>> update(@Valid @RequestBody UpdateRegistrationRq rq) {
        return ResponseEntity.ok(Result.success(registrationService.update(rq)));
    }

    @Operation(summary = "刪除臨打場次 (邏輯刪除)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<RegistrationRs>> delete(@PathVariable String id) {
        return ResponseEntity.ok(Result.success(registrationService.delete(id)));
    }

    @Operation(summary = "分頁查詢報名紀錄")
    @GetMapping
    public ResponseEntity<Result<Page<RegistrationRs>>> search(
            RegistrationSearchRq rq,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(Result.success(registrationService.search(rq, pageable)));
    }
}
