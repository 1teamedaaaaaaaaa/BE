package com.hoppin.infra.crawling.repository;

import com.hoppin.infra.crawling.entity.PromotionAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromotionAnalysisJobRepository extends JpaRepository<PromotionAnalysisJob, Long> {

    Optional<PromotionAnalysisJob> findTopByPromotion_IdOrderByCreatedAtDesc(Long promotionId);

    @Modifying
    @Query("""
    delete from PromotionAnalysisJob j
    where j.promotion.id = :promotionId
""")
    void deleteByPromotionId(@Param("promotionId") Long promotionId);

}
