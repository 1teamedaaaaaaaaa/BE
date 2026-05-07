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
     * 전체 링크 클릭 중 대표 홍보 링크 클릭 비중
     * trackingLinkTotalClickCount / totalLinkClickCount * 100
     */
    private double trackingClickShare;

    /**
     * 전체 링크 클릭 중 스트리밍 링크 클릭 비중
     * streamingLinkTotalClickCount / totalLinkClickCount * 100
     */
    private double streamingClickShare;

    /**
     * 게시물 1개당 평균 링크 클릭 수
     * totalLinkClickCount / contentCount
     */
    private double linkClickPerPost;
}