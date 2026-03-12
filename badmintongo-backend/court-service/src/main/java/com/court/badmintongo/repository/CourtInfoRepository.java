package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.CourtInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourtInfoRepository extends JpaRepository<CourtInfoPo, String>, JpaSpecificationExecutor<CourtInfoPo> {

    boolean existsByName(String name);
}
