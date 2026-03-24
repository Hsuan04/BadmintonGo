package com.court.badmintongo.controller;

import com.court.badmintongo.bean.vo.OptionVo;
import com.court.badmintongo.result.Result;
import com.court.badmintongo.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common/config")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CommonController {

    private final CommonService commonService;

    /**
     * 根據分類代碼獲取選單選項
     * 範例: /api/common/configs/COURT_CATEGORY
     */
    @GetMapping("/{typeCode}")
    public ResponseEntity<Result<List<OptionVo>>> getOptions(@PathVariable String typeCode) {
        List<OptionVo> options = commonService.getOptionsByTypeCode(typeCode);
        return ResponseEntity.ok(Result.success(options));
    }
}
