package com.court.badmintongo.repository;

import com.court.badmintongo.bean.po.RolePo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RolePo, Integer> {

    Optional<RolePo> findByName(String name);
}
