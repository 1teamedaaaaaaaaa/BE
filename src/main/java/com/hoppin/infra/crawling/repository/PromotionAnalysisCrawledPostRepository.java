package com.hoppin.infra.crawling.repository;

import com.hoppin.infra.crawling.entity.PromotionAnalysisCrawledPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionAnalysisCrawledPostRepository extends JpaRepository<PromotionAnalysisCrawledPost, Long> {

    void deleteByAnalysisJobId(Long analysisJobId);

    List<PromotionAnalysisCrawledPost> findByAnalysisJobIdOrderByTimestampDesc(Long analysisJobId);

    List<PromotionAnalysisCrawledPost> findByAnalysisJobIdOrderByCreatedAtDesc(Long analysisJobId);

    @Modifying
    @Query("""
    delete from PromotionAnalysisCrawledPost p
    where p.analysisJob.promotion.id = :promotionId
""")
    void deleteByPromotionId(@Param("promotionId") Long promotionId);
}
