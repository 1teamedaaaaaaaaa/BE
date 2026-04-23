package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionDiagnosisRepository extends JpaRepository<PromotionDiagnosis, Integer> {
}
