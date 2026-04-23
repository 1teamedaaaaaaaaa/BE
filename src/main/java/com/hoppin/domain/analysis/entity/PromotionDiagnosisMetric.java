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

    @Column(nullable = false)
    private Integer shareCount;

    @Column(nullable = false)
    private Integer profileVisitCount;

    @Column(nullable = false)
    private Integer linkClickCount;

    @Builder
    private PromotionDiagnosisMetric(
            Integer shareCount,
            Integer profileVisitCount,
            Integer linkClickCount
    ) {
        this.shareCount = shareCount;
        this.profileVisitCount = profileVisitCount;
        this.linkClickCount = linkClickCount;
    }

    void setPromotionDiagnosis(PromotionDiagnosis promotionDiagnosis) {
        this.promotionDiagnosis = promotionDiagnosis;
    }
}