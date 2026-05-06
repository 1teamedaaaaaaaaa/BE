package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionAnalysisJobRepository extends JpaRepository<PromotionAnalysisJob, Long> {
}
