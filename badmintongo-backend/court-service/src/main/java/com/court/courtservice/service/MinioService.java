package com.court.courtservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private final S3Presigner s3Presigner; // 使用剛剛定義的 Presigner

    @Value("${s3.bucket-name:court-bucket}")
    private String bucketName;

    public String getPresignedUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {
            // 建立取得物件的請求
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(7)) // 設定有效期限
                    .getObjectRequest(builder -> builder.bucket(bucketName).key(imagePath))
                    .build();

            // 產生預簽名請求
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            // 回傳完整的 URL 字串
            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("AWS S3 SDK 生成預簽名網址失敗: {}", e.getMessage());
            return null;
        }
    }
}
