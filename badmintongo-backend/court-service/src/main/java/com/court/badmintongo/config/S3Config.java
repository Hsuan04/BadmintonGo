package com.court.badmintongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    // 格式為 ${變數名:預設值}
    @Value("${s3.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${s3.access-key:test}")
    private String accessKey;

    @Value("${s3.secret-key:test1234}")
    private String secretKey;

    @Value("${s3.region:us-east-1}")
    private String region;

    @Value("${minio.bucket-name:court-images}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();

        initBucket(client);

        return client;
    }

    private void initBucket(S3Client client) {
        try {
            // 1. 檢查是否存在
            client.headBucket(software.amazon.awssdk.services.s3.model.HeadBucketRequest.builder()
                    .bucket(bucketName).build());
        } catch (software.amazon.awssdk.services.s3.model.NoSuchBucketException e) {
            // 2. 不存在則建立
            client.createBucket(software.amazon.awssdk.services.s3.model.CreateBucketRequest.builder()
                    .bucket(bucketName).build());

            // 3. 設定公開讀取權限 (Policy)
            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
            client.putBucketPolicy(software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policy)
                    .build());
        }
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
