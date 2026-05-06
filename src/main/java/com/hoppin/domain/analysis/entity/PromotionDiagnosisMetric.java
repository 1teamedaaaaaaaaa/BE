package com.hoppin.domain.analysis.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "promotion_diagnosis_metric")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionDiagnosisMetric extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosisMetricId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diagnosis_id", nullable = false, unique = true)
    private PromotionDiagnosis promotionDiagnosis;

    @Column(name = "content_count", nullable = false)
    private Integer contentCount;

    @Column(name = "total_like_count", nullable = false)
    private Integer totalLikeCount;

    @Column(name = "total_comment_count", nullable = false)
    private Integer totalCommentCount;

    @Column(name = "tracking_link_click_count", nullable = false)
    private Integer trackingLinkClickCount;

    @Column(name = "streaming_link_click_count", nullable = false)
    private Integer streamingLinkClickCount;

    @Column(name = "total_link_click_count", nullable = false)
    private Integer totalLinkClickCount;

    @Builder
    private PromotionDiagnosisMetric(
            Integer contentCount,
            Integer totalLikeCount,
            Integer totalCommentCount,
            Integer trackingLinkClickCount,
            Integer streamingLinkClickCount,
            Integer totalLinkClickCount
    ) {
        this.contentCount = contentCount;
        this.totalLikeCount = totalLikeCount;
        this.totalCommentCount = totalCommentCount;
        this.trackingLinkClickCount = trackingLinkClickCount;
        this.streamingLinkClickCount = streamingLinkClickCount;
        this.totalLinkClickCount = totalLinkClickCount;
    }

    void setPromotionDiagnosis(PromotionDiagnosis promotionDiagnosis) {
        this.promotionDiagnosis = promotionDiagnosis;
    }
}