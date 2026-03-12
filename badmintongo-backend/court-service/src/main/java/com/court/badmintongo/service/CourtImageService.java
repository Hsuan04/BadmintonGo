package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.CourtImagePo;
import com.court.badmintongo.repository.CourtImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourtImageService {

    private final S3Client s3Client;
    private final CourtImageRepository imageRepository;

    @Value("${s3.bucket-name:court-images}")
    private String bucketName;

    @Transactional
    public void uploadCourtImages(String courtId, MultipartFile[] files) {
        for (MultipartFile file : files) {
            // 1. 生成唯一檔案 Key (例如: courts/1/uuid_filename.jpg)
            String fileName = "courts/" + courtId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            try {
                // 2. 上傳到 MinIO
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(fileName)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                // 3. 儲存到資料庫
                CourtImagePo imagePo = CourtImagePo.builder()
                        .courtId(courtId)
                        .imageKey(fileName)
                        .isPrimary(false) // 預設非封面，可依需求調整
                        .build();
                imageRepository.save(imagePo);

            } catch (Exception e) {
                log.error("上傳失敗: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
            }
        }
    }
}
