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

    /**
     * 홍보 페이지 링크 복사용 URL
     * 기존 상세 응답의 trackingUrl과 필드명 통일
     */
    private String trackingUrl;

    /**
     * 기존 상세 응답의 streamingLinks와 필드명 통일
     */
    private List<StreamingLink> streamingLinks;

    private RealtimeStats realtimeStats;

    private DiagnosisSection diagnosisSection;

    @Getter
    @Builder
    public static class StreamingLink {
        private String streamingCode;

        /**
         * 원본 스트리밍 URL
         */
        private String url;

        /**
         * 클릭 집계용 redirect URL
         */
        private String clickUrl;

        private Integer displayOrder;

        /**
         * 해당 스트리밍 링크 클릭 수
         */
        private long clickCount;

        /**
         * 전체 스트리밍 클릭 중 해당 링크 비율
         */
        private double clickShareRate;
    }

    @Getter
    @Builder
    public static class RealtimeStats {
        /**
         * 홍보 링크 방문자 수
         */
        private long trackingClickCount;

        /**
         * 스트리밍 이동 총 횟수
         */
        private long streamingClickCount;
    }

    @Getter
    @Builder
    public static class DiagnosisSection {
        /**
         * NOT_STARTED, RUNNING, FAILED, COMPLETED
         */
        private String status;

        /**
         * COMPLETED일 때 진단 카드 목록
         */
        private List<DiagnosisCard> diagnosisCards;
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