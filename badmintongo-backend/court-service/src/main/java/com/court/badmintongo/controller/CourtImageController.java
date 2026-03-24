package com.court.badmintongo.controller;

import com.court.badmintongo.service.CourtImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/court")
@RequiredArgsConstructor
public class CourtImageController {

    private final CourtImageService courtImageService;

    @PostMapping(value = "/{courtId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(@PathVariable String courtId, @RequestPart("files") MultipartFile[] files, @RequestParam("primaryIndex") int primaryIndex) {
        courtImageService.uploadCourtImages(courtId, files, primaryIndex);
        return ResponseEntity.ok("成功上傳 " + files.length + " 張圖片");
    }

    /**
     * 取得該場地的所有圖片預簽名網址 (供前端刷新網址使用)
     */
    @GetMapping("/{courtId}/images")
    public ResponseEntity<List<String>> getCourtImages(@PathVariable String courtId) {
        List<String> imageUrls = courtImageService.getPresignedUrlsByCourtId(courtId);
        return ResponseEntity.ok(imageUrls);
    }

}