package com.hoppin.domain.analysis.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "promotion_action_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionActionPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionPlanId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private PromotionDiagnosis promotionDiagnosis;

    @Column(nullable = false)
    private Integer actionOrder;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String reason;

    @Column(length = 100, nullable = false)
    private String metric;

    @Column(length = 1000, nullable = false)
    private String example;

    @Builder
    private PromotionActionPlan(
            Integer actionOrder,
            String title,
            String reason,
            String metric,
            String example
    ) {
        this.actionOrder = actionOrder;
        this.title = title;
        this.reason = reason;
        this.metric = metric;
        this.example = example;
    }

    void setPromotionDiagnosis(PromotionDiagnosis promotionDiagnosis) {
        this.promotionDiagnosis = promotionDiagnosis;
    }
}