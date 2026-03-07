package com.court.badmintongo.controller;


import com.badmintongo.result.Result;
import com.court.badmintongo.bean.vo.CreateRegistrationRq;
import com.court.badmintongo.bean.vo.RegistrationRs;
import com.court.badmintongo.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

//    @Operation(summary = "更新臨打場次")
//    @PutMapping("/{id}")
//    public ResponseEntity<Result<registrationRs>> update(@PathVariable Long id, @Valid @RequestBody UpdateregistrationRq rq) {
//        rq.setregistrationId(id); // 確保 ID 以路徑為準
//        return ResponseEntity.ok(Result.success(registrationService.update(rq)));
//    }
//
//    @Operation(summary = "刪除臨打場次 (邏輯刪除)")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Result<registrationRs>> delete(@PathVariable Long id) {
//        return ResponseEntity.ok(Result.success(registrationService.softDelete(id)));
//    }
//
//    @Operation(summary = "分頁查詢臨打場次")
//    @GetMapping
//    public ResponseEntity<Result<Page<registrationRs>>> search(
//            @RequestParam Map<String, String> params,
//            @PageableDefault(sort = "sessionDate") Pageable pageable) {
//        return ResponseEntity.ok(Result.success(registrationService.search(params, pageable)));
//    }
}
