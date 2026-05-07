package com.hoppin.infra.crawling.dto.response;

import java.util.List;

public record AnalysisCrawlerResultResponse(
        Long analysisJobId,
        Integer contentCount,
        Integer totalLikeCount,
        Integer totalCommentCount,
        List<AnalysisCrawledPostResponse> posts
) {
}
