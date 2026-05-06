package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionAnalysisCrawledPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionAnalysisCrawledPostRepository extends JpaRepository<PromotionAnalysisCrawledPost, Long> {

    void deleteByAnalysisJobId(Long analysisJobId);

    List<PromotionAnalysisCrawledPost> findByAnalysisJobIdOrderByTimestampDesc(Long analysisJobId);

    List<PromotionAnalysisCrawledPost> findByAnalysisJobIdOrderByCreatedAtDesc(Long analysisJobId);
}
