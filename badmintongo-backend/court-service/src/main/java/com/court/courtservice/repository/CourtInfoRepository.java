package com.court.courtservice.repository;

import com.court.courtservice.bean.po.CourtInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourtInfoRepository extends JpaRepository<CourtInfoPo, Integer>, JpaSpecificationExecutor<CourtInfoPo> {

    boolean existsByName(String name);
}
