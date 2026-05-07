package com.hoppin.infra.crawling.dto.response;

import com.hoppin.infra.crawling.entity.PromotionAnalysisCrawledPost;

public record AnalysisCrawledPostResponse(
        String mediaId,
        String caption,
        String mediaType,
        String permalink,
        String timestamp,
        Integer likeCount,
        Integer commentCount
) {

    public static AnalysisCrawledPostResponse from(PromotionAnalysisCrawledPost post) {
        return new AnalysisCrawledPostResponse(
                post.getMediaId(),
                post.getCaption(),
                post.getMediaType(),
                post.getPermalink(),
                post.getTimestamp(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}
