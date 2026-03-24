package com.court.badmintongo.bean.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "court_holiday_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtHolidayInfoPo {

    @Id
    @Column(name = "court_holiday_id")
    private String courtHolidayId;

    @Column(name = "court_id")
    private String courtId;

    @Column(name = "holiday_date")
    private LocalDate holidayDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
