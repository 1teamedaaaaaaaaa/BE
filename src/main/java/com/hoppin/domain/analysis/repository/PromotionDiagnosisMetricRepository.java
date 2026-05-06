package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionDiagnosisMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionDiagnosisMetricRepository extends JpaRepository<PromotionDiagnosisMetric, Integer> {
}
