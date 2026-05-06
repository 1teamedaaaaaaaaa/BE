package com.hoppin.domain.analysis.repository;

import com.hoppin.domain.analysis.entity.PromotionAnalysisCrawledPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionAnalysisCrawledPostRepository extends JpaRepository<PromotionAnalysisCrawledPost, Long> {

    void deleteByAnalysisJobId(Long analysisJobId);

    java.util.List<PromotionAnalysisCrawledPost> findByAnalysisJobIdOrderByTimestampDesc(Long analysisJobId);
}
