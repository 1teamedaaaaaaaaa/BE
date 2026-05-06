package com.hoppin.infra.ai.dto.request;

public record AnalysisJobWebhookRequest(
        Long analysisJobId,
        Long promotionId
) {
}
