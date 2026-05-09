package com.hoppin.domain.analysis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PromotionAnalysisPageResponse {

    private Long promotionId;

    private PromotionInfo promotionInfo;
    private RealtimeStats realtimeStats;
    private List<StreamingStat> streamingStats;

    private DiagnosisSection diagnosisSection;

    @Getter
    @Builder
    public static class PromotionInfo {
        private String albumName;
        private String imageUrl;
        private LocalDate releaseDate;
        private LocalDate promotionStartDate;
        private String promotionTrackingUrl;
    }

    @Getter
    @Builder
    public static class RealtimeStats {
        private long promotionPageVisitCount;
        private long streamingClickCount;
    }

    @Getter
    @Builder
    public static class StreamingStat {
        private String streamingCode;
        private String displayName;
        private String url;
        private long clickCount;
        private double clickShareRate;
    }

    @Getter
    @Builder
    public static class DiagnosisSection {
        private String status;

        /**
         * COMPLETED일 때만 값 있음
         */
        private DiagnosisCard latestDiagnosis;
    }

    @Getter
    @Builder
    public static class DiagnosisCard {
        private Long diagnosisId;
        private String diagnosedDate;
        private String bottleneckType;
        private String headline;
        private String actionTitle;
        private boolean unread;
    }
}