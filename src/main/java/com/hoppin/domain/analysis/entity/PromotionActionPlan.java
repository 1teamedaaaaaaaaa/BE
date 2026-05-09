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

    @Column(length = 300, nullable = false)
    private String metric;

    @Column(length = 2000, nullable = false)
    private String details;

    @Builder
    private PromotionActionPlan(
            Integer actionOrder,
            String title,
            String metric,
            String details
    ) {
        this.actionOrder = actionOrder;
        this.title = title;
        this.metric = metric;
        this.details = details;
    }

    void setPromotionDiagnosis(PromotionDiagnosis promotionDiagnosis) {
        this.promotionDiagnosis = promotionDiagnosis;
    }
}