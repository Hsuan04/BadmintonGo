package com.badmintongo.bean.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "court_image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtImagePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    private Integer courtId;

    private String imageKey; // 存 S3 裡的路徑

    private Boolean isPrimary;

    private OffsetDateTime createdAt;
}
