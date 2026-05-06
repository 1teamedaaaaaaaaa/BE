package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionActionPlanRepository extends JpaRepository<PromotionActionPlan, Integer> {
    void deleteByPromotionDiagnosis_DiagnosisId(Long diagnosisId);
}
