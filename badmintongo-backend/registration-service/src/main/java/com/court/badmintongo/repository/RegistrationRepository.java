package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.RegistrationInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationInfoPo, String>, JpaSpecificationExecutor<RegistrationInfoPo> {

    /**
     * 檢查該場次是否已有相同的聯絡資訊 (用於防止重複報名)
     */
    boolean existsBySessionIdAndContactInfo(String sessionId, String contactInfo);

    /**
     * 檢查生成的 UUID (userId) 是否已存在 (用於確保訪客 ID 唯一)
     */
    boolean existsByUserId(String userId);

    /**
     * 根據 userId 查詢報名紀錄 (方便訪客透過 ID 找回資料)
     */
    Optional<RegistrationInfoPo> findByUserId(String userId);

}
