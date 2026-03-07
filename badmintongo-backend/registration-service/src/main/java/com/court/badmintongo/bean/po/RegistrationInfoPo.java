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
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column
    private Long userId;

    @Column(nullable = false)
    private String userType; // MEMBER, GUEST

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer skillLevel;

    @Column(nullable = false)
    private String contactType; // Phone 或 LINE

    @Column(nullable = false)
    private String contactInfo;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer queueOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}