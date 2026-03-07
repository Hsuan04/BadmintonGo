package com.court.courtservice.controller;

import com.court.courtservice.service.CourtImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtImageController {

    private final CourtImageService courtImageService;

    // 使用 MultipartFormData 接收檔案
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(@PathVariable Integer id, @RequestPart("files") MultipartFile[] files) {
        courtImageService.uploadCourtImages(id, files);
        return ResponseEntity.ok("成功上傳 " + files.length + " 張圖片");
    }
}