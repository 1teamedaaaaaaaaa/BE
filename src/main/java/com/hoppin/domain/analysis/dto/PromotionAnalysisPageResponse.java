package com.hoppin.domain.analysis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PromotionAnalysisPageResponse {

    private Long promotionId;

    private String activityName;
    private String songTitle;
    private LocalDate releaseDate;
    private String imageUrl;
    private String shortDescription;
    private LocalDateTime createdAt;
    private String trackingUrl;

    private List<StreamingLink> streamingLinks;
    private RealtimeStats realtimeStats;

    private List<AnalysisDiagnosisItem> diagnosis;
    private DiagnosisPage diagnosisPage;

    @Getter
    @Builder
    public static class StreamingLink {
        private String streamingCode;
        private String url;
        private String clickUrl;
        private Integer displayOrder;
        private long clickCount;
        private double clickShareRate;
    }

    @Getter
    @Builder
    public static class RealtimeStats {
        private long trackingClickCount;
        private long streamingClickCount;
    }

    @Getter
    @Builder
    public static class AnalysisDiagnosisItem {
        private String status;
        private Long diagnosisId;
        private String diagnosedDate;
        private String bottleneckType;
        private String headline;
        private String actionTitle;
        private boolean unread;
    }

    @Getter
    @Builder
    public static class DiagnosisPage {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
    }
}