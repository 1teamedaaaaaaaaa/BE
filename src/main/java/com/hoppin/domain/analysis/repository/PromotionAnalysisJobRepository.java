package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionAnalysisJobRepository extends JpaRepository<PromotionAnalysisJob, Long> {

    Optional<PromotionAnalysisJob> findTopByPromotion_IdOrderByCreatedAtDesc(Long promotionId);
}
