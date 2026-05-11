package com.hoppin.infra.crawling.client;

import com.hoppin.infra.crawling.dto.request.AnalysisJobWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
@Slf4j
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
            log.warn("Analysis automation webhook skipped because webhook URL is blank. analysisJobId={}, promotionId={}",
                    analysisJobId, promotionId);
            return;
        }

        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AnalysisJobWebhookRequest(analysisJobId,
                                                promotionId))
                .retrieve()
                .toBodilessEntity();
    }
}
