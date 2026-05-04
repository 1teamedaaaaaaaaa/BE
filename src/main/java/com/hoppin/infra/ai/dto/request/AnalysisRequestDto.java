package com.hoppin.infra.ai.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalysisRequestDto {

    // 분석 시작 날짜
    private String sinceDate;

    // 해당 기간 내 인스타 게시물 개수
    private int contentCount;

    // 인스타 게시물 전체 도달 수 합계
    private int reachCount;

    // 인스타 게시물 전체 공유 수 합계
    private int shareCount;

    // 인스타 게시물 전체 프로필 방문 수 합계
    private int profileVisitCount;

    // 홍보 링크 전체 클릭 수 합계
    private int linkClickCount;

    // 채널별 홍보 링크 클릭 수
    private List<ChannelClickSummary> channelClicks;

    // 성과가 좋은 게시물 후보
    private List<PostMetricSummary> topCandidatePosts;

    // 성과가 낮은 게시물 후보
    private List<PostMetricSummary> lowCandidatePosts;

    // 분석 대상 홍보 링크
    private String promoLink;

    // 사용자가 직접 입력한 고민
    private String mainPainPoint;
    // 사용자가 직접 입력한 제약사항
    private String mainResourceConstraint;
    private String analysisMode; // PRE_CAMPAIGN or POST_CAMPAIGN
    // 홍보발매 시작날짜
    private String releaseDate;

    @Getter
    @Setter
    public static class ChannelClickSummary {
        private String channel;
        private int clickCount;
    }

    @Getter
    @Setter
    public static class PostMetricSummary {
        private String mediaId;
        private String caption;
        private String mediaType;
        private String permalink;
        private String timestamp;

        private int reachCount;
        private int shareCount;
        private int profileVisitCount;
    }
}