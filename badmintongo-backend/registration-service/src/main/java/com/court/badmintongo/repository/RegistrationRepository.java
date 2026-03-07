package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.RegistrationInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationInfoPo, Long>, JpaSpecificationExecutor<RegistrationInfoPo> {

}
