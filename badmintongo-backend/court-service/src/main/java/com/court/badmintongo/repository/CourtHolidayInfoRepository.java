package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.CourtHolidayInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CourtHolidayInfoRepository extends JpaRepository<CourtHolidayInfoPo, String> {

    @Query("SELECT h FROM CourtHolidayInfoPo h WHERE h.courtId = :courtId AND h.holidayDate >= :today ORDER BY h.holidayDate ASC")
    List<CourtHolidayInfoPo> findUpcomingHolidays(@Param("courtId") String courtId, @Param("today") LocalDate today);

    @Query("SELECT h FROM CourtHolidayInfoPo h " +
            "WHERE h.courtId IN :courtIds " +
            "AND h.holidayDate >= :today " +
            "ORDER BY h.holidayDate ASC")
    List<CourtHolidayInfoPo> findUpcomingHolidaysByCourtIds(
            @Param("courtIds") List<String> courtIds,
            @Param("today") LocalDate today
    );

    void deleteByCourtId(String courtId);

}
