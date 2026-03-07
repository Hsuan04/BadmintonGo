package com.badmintongo.repository;

import com.badmintongo.bean.po.CourtImagePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourtImageRepository extends JpaRepository<CourtImagePo, Integer> {

    List<CourtImagePo> findByCourtId(Integer courtId);

    List<CourtImagePo> findByCourtIdIn(List<Integer> courtId);

    /**
     * 刪除某個場地中指定的複數圖片紀錄
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CourtImagePo i WHERE i.courtId = :courtId AND i.imageKey IN :imageKeys")
    void deleteByCourtIdAndImageKeyIn(@Param("courtId") Integer courtId, @Param("imageKeys") List<String> imageKeys);
}
