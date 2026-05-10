package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionDiagnosisRepository extends JpaRepository<PromotionDiagnosis, Long> {
    List<PromotionDiagnosis> findByMusicPromotion_Id(Long promotionId);

    Optional<PromotionDiagnosis> findTopByMusicPromotion_IdOrderByDiagnosedAtDesc(Long promotionId);

    List<PromotionDiagnosis> findByMusicPromotion_IdOrderByDiagnosedAtDesc(Long promotionId);

    Page<PromotionDiagnosis> findPageByMusicPromotion_IdOrderByDiagnosedAtDesc(
            Long promotionId,
            Pageable pageable
    );

}
