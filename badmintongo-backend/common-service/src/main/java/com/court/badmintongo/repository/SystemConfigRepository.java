package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.SystemConfigId;
import com.court.badmintongo.bean.po.SystemConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemConfigRepository extends JpaRepository<SystemConfigPo, SystemConfigId> {

    @Query("SELECT s FROM SystemConfigPo s WHERE s.typeCode = :typeCode AND s.isEnabled = true ORDER BY s.sortOrder ASC")
    List<SystemConfigPo> findActiveConfigs(@Param("typeCode") String typeCode);
}