package com.hoppin.infra.crawling.dto.response;

public record AnalysisJobCreateResponse(
        Long analysisJobId,
        String status
) {
}
