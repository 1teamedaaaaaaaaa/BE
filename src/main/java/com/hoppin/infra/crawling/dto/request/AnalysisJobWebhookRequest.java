package com.hoppin.infra.crawling.dto.request;

public record AnalysisJobWebhookRequest(
        Long analysisJobId,
        Long promotionId
) {
}
