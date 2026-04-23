package com.hoppin.domain.analysis.entity;


import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.analysis.enumtype.DiagnosisStatus;
import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "promotion_diagnosis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionDiagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosisId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private MusicPromotion musicPromotion;

    @Column(length = 200, nullable = false)
    private String headline;

    @Column(length = 100, nullable = false)
    private String bottleneckType;

    @Column(length = 200, nullable = false)
    private String highlightSection;

    @Column(length = 1000, nullable = false)
    private String interpretation;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private DiagnosisStatus status;

    @Column(nullable = false)
    private LocalDateTime diagnosedAt;

    @OneToOne(mappedBy = "promotionDiagnosis", cascade = CascadeType.ALL, orphanRemoval = true)
    private PromotionDiagnosisMetric diagnosisMetric;

    @OneToMany(mappedBy = "promotionDiagnosis", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("actionOrder ASC")
    private List<PromotionActionPlan> actionPlans = new ArrayList<>();

    @Builder
    private PromotionDiagnosis(
            MusicPromotion musicPromotion,
            String headline,
            String bottleneckType,
            String highlightSection,
            String interpretation,
            DiagnosisStatus status,
            LocalDateTime diagnosedAt
    ) {
        this.musicPromotion = musicPromotion;
        this.headline = headline;
        this.bottleneckType = bottleneckType;
        this.highlightSection = highlightSection;
        this.interpretation = interpretation;
        this.status = status;
        this.diagnosedAt = diagnosedAt;
    }

    public void assignMetric(PromotionDiagnosisMetric diagnosisMetric) {
        this.diagnosisMetric = diagnosisMetric;
        diagnosisMetric.setPromotionDiagnosis(this);
    }

    public void addActionPlan(PromotionActionPlan actionPlan) {
        this.actionPlans.add(actionPlan);
        actionPlan.setPromotionDiagnosis(this);
    }
}