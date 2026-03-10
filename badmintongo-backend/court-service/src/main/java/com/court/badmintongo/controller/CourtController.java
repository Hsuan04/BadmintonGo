package com.court.badmintongo.controller;

import com.court.badmintongo.bean.vo.CourtRs;
import com.court.badmintongo.bean.vo.CreateCourtRq;
import com.court.badmintongo.bean.vo.UpdateCourtRq;
import com.court.badmintongo.result.Result;
import com.court.badmintongo.service.CourtService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    /**
     * 1. 新增球場
     * Body: JSON 格式的 CourtInfoPo
     */
    @Operation(summary = "createCourt", description = "create a court information")
    @PostMapping
    public ResponseEntity<Result<CourtRs>> create(@Valid @RequestBody CreateCourtRq createCourtRq) {
        CourtRs rs = courtService.create(createCourtRq);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(rs));
    }

    /**
     * 2. 刪除球場
     * @param id 場地id
     */
    @Operation(summary = "deleteCourt", description = "update court status to 4")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<CourtRs>> deleteCourt(@PathVariable Integer id) {
        CourtRs deletedCourt = courtService.softDelete(id);
        return ResponseEntity.ok(Result.success(deletedCourt));
    }

    /**
     * 3. 更新球場 (PUT)
     * 路徑帶上 ID，Body 帶上要修改的內容
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<CourtRs>> update(@PathVariable Integer id, @RequestBody UpdateCourtRq request) {
        return ResponseEntity.ok(Result.success(courtService.update(id, request)));
    }

    /**
     * 4. 條件查詢與分頁 (GET)
     * params: 查詢條件
     * viewMode: 身份 todo 後續要換成 security 的身份驗證
     * pageable: 分頁筆數物件
     * 範例: /api/v1/courts?name=大安&category=私人球場&sportType=BADMINTON&sort=category,desc
     */
    @Operation(summary = "查詢場地", description = "分頁查詢場地資訊")
    @GetMapping
    public ResponseEntity<Result<Page<CourtRs>>> searchCourts( @RequestParam Map<String, String> params,
                                                               @RequestParam(defaultValue = "USER") String viewMode,
                                                               @PageableDefault(sort = "category", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CourtRs> result = courtService.searchCourts(params, viewMode, pageable);
        return ResponseEntity.ok(Result.success(result));
    }

}
