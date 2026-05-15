package com.hoppin.infra.crawling.service;

import com.hoppin.infra.crawling.client.AnalysisAutomationWebhookClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisAutomationWebhookDispatcher {

    private final AnalysisAutomationWebhookClient analysisAutomationWebhookClient;

    @Async("analysisWebhookExecutor")
    public void dispatch(Long analysisJobId, Long promotionId) {
        try {
            analysisAutomationWebhookClient.trigger(analysisJobId, promotionId);
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to dispatch analysis automation webhook. analysisJobId={}, promotionId={}, message={}",
                    analysisJobId,
                    promotionId,
                    exception.getMessage(),
                    exception
            );
        }
    }
}
