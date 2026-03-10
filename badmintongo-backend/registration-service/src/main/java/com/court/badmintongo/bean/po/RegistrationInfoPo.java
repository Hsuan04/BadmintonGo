package com.court.badmintongo.bean.po;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * 報名資訊表
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_info")
public class RegistrationInfoPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String sessionId;

    @Column
    private String userId;

    @Column(nullable = false)
    private String userType; // MEMBER, GUEST, ADMIN

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer skillLevel;

    @Column(nullable = false)
    private String contactType;   // Phone 或 LINE

    @Column(nullable = false)
    private String contactInfo;   // 0912345678 or lineID.000

    @Column(nullable = false)
    private Integer status;      // 0:備取 , 1:正取

    @Column(nullable = false)
    private Integer queueOrder;  //報名排序

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}