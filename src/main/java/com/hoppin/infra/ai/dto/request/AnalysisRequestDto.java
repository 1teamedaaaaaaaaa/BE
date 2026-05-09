package com.hoppin.infra.ai.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalysisRequestDto {

    private Long promotionId;
    private Long analysisJobId;

    private String sinceDate;
    private String releaseDate;
    private String instagramUsername;
    private String analysisMode;

    private String promoLink;

    private String mainPainPoint;
    private String mainResourceConstraint;

    private InstagramSummary instagramSummary;
    private LinkClickSummary linkClickSummary;
    private List<CrawledPostSummary> posts;

    @Getter
    @Builder
    public static class InstagramSummary {
        private long contentCount;
        private long followerCount;
        private long totalLikeCount;
        private long totalCommentCount;
    }

    @Getter
    @Builder
    public static class LinkClickSummary {
        private long trackingLinkTotalClickCount;
        private long streamingLinkTotalClickCount;

        private List<TrackingLinkClickSummary> trackingLinks;
        private List<StreamingLinkClickSummary> streamingLinks;
    }

    @Getter
    @Builder
    public static class TrackingLinkClickSummary {
        private String channel;
        private String url;
        private long clickCount;
    }

    @Getter
    @Builder
    public static class StreamingLinkClickSummary {
        private String streamingCode;
        private String url;
        private long clickCount;
    }

    @Getter
    @Builder
    public static class CrawledPostSummary {
        private String mediaId;
        private String caption;
        private String mediaType;
        private String permalink;
        private String timestamp;

        private long likeCount;
        private long commentCount;
    }
}
