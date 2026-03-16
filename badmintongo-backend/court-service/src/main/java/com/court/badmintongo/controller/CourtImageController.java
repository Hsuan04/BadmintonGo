package com.court.badmintongo.controller;

import com.court.badmintongo.service.CourtImageService;
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
    @PostMapping(value = "/{courtId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(@PathVariable String courtId, @RequestPart("files") MultipartFile[] files, @RequestParam("primaryIndex") int primaryIndex) {
        courtImageService.uploadCourtImages(courtId, files, primaryIndex);
        return ResponseEntity.ok("成功上傳 " + files.length + " 張圖片");
    }
}