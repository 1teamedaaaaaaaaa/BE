package com.hoppin.domain.analysis.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromotionDiagnosisDetailResponse {

    private String headline;
    private String periodLabel;

    private SummaryMetrics summaryMetrics;
    private Diagnosis diagnosis;
    private Action action;

    @Getter
    @Builder
    public static class SummaryMetrics {
        private double followerEngagementRate;
        private double promoClickRateByEngagement;
        private double streamingClickRateByPromoClick;
    }

    @Getter
    @Builder
    public static class Diagnosis {
        private String highlightFrom;
        private String highlightTo;
    }

    @Getter
    @Builder
    public static class Action {
        private String title;
        private String metric;
        private String details;
    }
}