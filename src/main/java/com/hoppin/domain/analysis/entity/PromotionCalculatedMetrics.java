package com.hoppin.domain.analysis.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "promotion_calculated_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionCalculatedMetrics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calculated_metrics_id")
    private Long calculatedMetricsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false, unique = true)
    private PromotionDiagnosis promotionDiagnosis;

    @Column(nullable = false)
    private Double avgLikePerPost;

    @Column(nullable = false)
    private Double avgCommentPerPost;

    @Column(nullable = false)
    private Double commentRateByLike;

    @Column(nullable = false)
    private Double streamingClickShare;

    @Column(nullable = false)
    private Double followerEngagementRate;

    @Column(nullable = false)
    private Double promoClickRateByEngagement;

    @Column(nullable = false)
    private Double streamingClickRateByPromoClick;

    @Builder
    private PromotionCalculatedMetrics(
            Double avgLikePerPost,
            Double avgCommentPerPost,
            Double commentRateByLike,
            Double streamingClickShare,
            Double followerEngagementRate,
            Double promoClickRateByEngagement,
            Double streamingClickRateByPromoClick
    ) {
        this.avgLikePerPost = avgLikePerPost == null ? 0.0 : avgLikePerPost;
        this.avgCommentPerPost = avgCommentPerPost == null ? 0.0 : avgCommentPerPost;
        this.commentRateByLike = commentRateByLike == null ? 0.0 : commentRateByLike;
        this.streamingClickShare = streamingClickShare == null ? 0.0 : streamingClickShare;
        this.followerEngagementRate = followerEngagementRate == null ? 0.0 : followerEngagementRate;
        this.promoClickRateByEngagement = promoClickRateByEngagement == null ? 0.0 : promoClickRateByEngagement;
        this.streamingClickRateByPromoClick = streamingClickRateByPromoClick == null ? 0.0 : streamingClickRateByPromoClick;
    }

    public void assignDiagnosis(PromotionDiagnosis promotionDiagnosis) {
        this.promotionDiagnosis = promotionDiagnosis;
    }
}