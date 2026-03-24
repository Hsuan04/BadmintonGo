package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.CourtImagePo;
import com.court.badmintongo.repository.CourtImageRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourtImageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CourtImageRepository courtImageRepository;

    @Value("${s3.bucket-name:court-images}")
    private String bucketName;

    /**
     * 上傳球場圖片至 S3
     * @param courtId 場地key
     * @param files 場地圖片陣列
     * @param primaryIndex 封面照索引值(作為首頁顯示使用)
     */
    public void uploadCourtImages(String courtId, MultipartFile[] files, int primaryIndex) {

        //逐筆上傳球場圖片
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            //處理檔案名稱，
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));  //取得副檔名
            }
            String filePath = courtId.concat("/").concat(String.valueOf(UUID.randomUUID())).concat(extension);  //用 UUID 作為檔案名稱，防止有特殊符號的檔案名稱

            try {
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucketName)  //指定的儲存空間
                                .key(filePath)       //儲存位置
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                //組建 courtImagePo
                CourtImagePo imagePo = CourtImagePo.builder()
                        .imageId(TSID.fast().toString())
                        .courtId(courtId)
                        .imageKey(filePath)
                        .isPrimary(i == primaryIndex)
                        .orderNum(i)
                        .build();

                courtImageRepository.save(imagePo);  //儲存球場圖片資料

            } catch (IOException e) {
                log.error("上傳失敗: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
            }
        }
    }

    /**
     * 刪除 S3 的球場圖片
     * @param courtId 場地key
     */
    public void deleteByCourtId(String courtId) {

        // 1. 找出該球場所有的圖片資訊
        List<CourtImagePo> images = courtImageRepository.findByCourtId(courtId);

        if (images.isEmpty()) return;

        // 2. 逐一從 MinIO (S3) 刪除實體檔案
        for (CourtImagePo img : images) {
            try {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(img.getImageKey())
                                .build());
            } catch (Exception e) {
                log.error("無法刪除 MinIO 檔案: {}", img.getImageKey(), e);
            }
        }

        // 3. 刪除資料庫裡的圖片資料
        courtImageRepository.deleteByCourtId(courtId);
    }

    /**
     * 取得該場地所有圖片的預簽名網址
     * @param courtId 場地key
     * return List<String> 圖片在S3k的路徑
     */
    public List<String> getPresignedUrlsByCourtId(String courtId) {
        List<CourtImagePo> images = courtImageRepository.findByCourtId(courtId);

        return images.stream()
                .map(img -> getPresignedUrl(img.getImageKey()))  //透由圖片路徑取得預簽名網址
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 產生單一圖片的預簽名網址 (有效期限 7 天)
     */
    public String getPresignedUrl(String key) {
        try {
            //取得 S3 物件
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            //定義網址規範
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(7))          //7天有效期限
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("產生成預簽名網址失敗: {}", key, e);
            return null;
        }
    }


}
