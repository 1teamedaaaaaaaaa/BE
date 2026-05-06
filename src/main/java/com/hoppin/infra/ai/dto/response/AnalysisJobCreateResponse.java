package com.hoppin.infra.ai.dto.response;

public record AnalysisJobCreateResponse(
        Long analysisJobId,
        String status
) {
}
