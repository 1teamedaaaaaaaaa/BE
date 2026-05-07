package com.hoppin.infra.crawling.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "promotion_analysis_crawled_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionAnalysisCrawledPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_crawled_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_job_id", nullable = false)
    private PromotionAnalysisJob analysisJob;

    @Column(name = "media_id", length = 100)
    private String mediaId;

    @Column(length = 2000)
    private String caption;

    @Column(name = "media_type", length = 50)
    private String mediaType;

    @Column(length = 1000, nullable = false)
    private String permalink;

    @Column(length = 100)
    private String timestamp;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @Builder
    private PromotionAnalysisCrawledPost(
            PromotionAnalysisJob analysisJob,
            String mediaId,
            String caption,
            String mediaType,
            String permalink,
            String timestamp,
            Integer likeCount,
            Integer commentCount
    ) {
        this.analysisJob = analysisJob;
        this.mediaId = mediaId;
        this.caption = caption;
        this.mediaType = mediaType;
        this.permalink = permalink;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
