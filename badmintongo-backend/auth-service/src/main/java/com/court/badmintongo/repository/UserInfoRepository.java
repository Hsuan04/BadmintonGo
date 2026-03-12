package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.UserInfoPo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserInfoRepository extends JpaRepository<UserInfoPo, UUID> {
    Optional<UserInfoPo> findByEmail(String email);
}
