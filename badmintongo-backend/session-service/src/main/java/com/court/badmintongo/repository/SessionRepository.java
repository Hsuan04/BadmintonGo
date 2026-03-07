package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.SessionInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<SessionInfoPo, String>, JpaSpecificationExecutor<SessionInfoPo> {

    /**
     * 根據日期查詢所有臨打場次
     */
    List<SessionInfoPo> findBySessionDateOrderByStartTimeAsc(LocalDate sessionDate);

    /**
     * 根據場地 ID 查詢該場地的所有歷史臨打紀錄
     */
    List<SessionInfoPo> findByCourtIdOrderBySessionDateDesc(String courtId);

    /**
     * 核心查詢：根據用戶程度篩選適合的場次
     * 邏輯：用戶程度必須在場次要求的 minLevel 與 maxLevel 之間
     */
    @Query("SELECT p FROM SessionInfoPo p WHERE :userLevel BETWEEN p.minLevel AND p.maxLevel AND p.status = 1")
    List<SessionInfoPo> findSuitableSessions(@Param("userLevel") Integer userLevel);

    /**
     * 查詢某日期後所有開放報名的場次
     */
    List<SessionInfoPo> findBySessionDateGreaterThanEqualAndStatusOrderBySessionDateAsc(LocalDate date, Integer status);
}
