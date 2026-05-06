package com.hoppin.infra.ai.client;

import com.hoppin.infra.ai.dto.request.AnalysisJobWebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnalysisAutomationWebhookClient {

    private final RestClient restClient = RestClient.create();
    private final String webhookUrl;

    public AnalysisAutomationWebhookClient(
            @Value("${app.analysis-automation-webhook-url:}") String webhookUrl
    ) {
        this.webhookUrl = webhookUrl;
    }

    public void trigger(Long analysisJobId, Long promotionId) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AnalysisJobWebhookRequest(analysisJobId, promotionId))
                .retrieve()
                .toBodilessEntity();
    }
}
