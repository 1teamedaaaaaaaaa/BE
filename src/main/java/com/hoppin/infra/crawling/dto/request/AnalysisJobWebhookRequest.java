package com.hoppin.infra.crawling.dto.request;

import java.time.LocalDate;

public record AnalysisJobWebhookRequest(
        Long analysisJobId,
        Long promotionId
) {
}
