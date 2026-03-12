package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.CourtOpenInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourtOpenInfoRepository extends JpaRepository<CourtOpenInfoPo, String> {

    List<CourtOpenInfoPo> findByCourtId(String courtId);

    List<CourtOpenInfoPo> findByCourtIdIn(List<String> courtIds);

    /**
     * 根據場地 ID 刪除所有時段
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CourtOpenInfoPo o WHERE o.courtId = :courtId")
    void deleteByCourtId(@Param("courtId") String courtId);
}
