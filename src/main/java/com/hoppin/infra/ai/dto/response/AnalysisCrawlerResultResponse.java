package com.hoppin.infra.ai.dto.response;

import java.util.List;

public record AnalysisCrawlerResultResponse(
        Long analysisJobId,
        Integer contentCount,
        Integer totalLikeCount,
        Integer totalCommentCount,
        List<AnalysisCrawledPostResponse> posts
) {
}
