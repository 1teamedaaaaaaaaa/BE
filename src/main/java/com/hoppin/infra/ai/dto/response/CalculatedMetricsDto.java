package com.hoppin.infra.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatedMetricsDto {

    /**
     * 게시물 1개당 평균 좋아요 수
     * totalLikeCount / contentCount
     */
    private double avgLikePerPost;

    /**
     * 게시물 1개당 평균 댓글 수
     * totalCommentCount / contentCount
     */
    private double avgCommentPerPost;

    /**
     * 좋아요 대비 댓글 비율
     * totalCommentCount / totalLikeCount * 100
     */
    private double commentRateByLike;

    /**
     * 전체 링크 클릭 중 스트리밍 링크 클릭 비중
     * streamingLinkTotalClickCount / totalLinkClickCount * 100
     * totalLinkClickCount = trackingLinkTotalClickCount + streamingLinkTotalClickCount
     */
    private double streamingClickShare;

    /**
     * 팔로워 대비 게시글 반응률
     * (totalLikeCount + totalCommentCount) / followerCount * 100
     */
    private double followerEngagementRate;

    /**
     * 반응 대비 홍보 링크 클릭률
     * trackingLinkTotalClickCount / (totalLikeCount + totalCommentCount) * 100
     */
    private double promoClickRateByEngagement;

    /**
     * 홍보 링크 클릭 대비 스트리밍 링크 클릭률
     * streamingLinkTotalClickCount / trackingLinkTotalClickCount * 100
     */
    private double streamingClickRateByPromoClick;
}