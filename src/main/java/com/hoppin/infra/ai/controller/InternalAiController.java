package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisService;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import com.hoppin.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/ai")
@RequiredArgsConstructor
public class InternalAiController {

    private final AiService aiService;
    private final PromotionAnalysisService promotionAnalysisService;
    private final EmailService emailService;

    @PostMapping("/analyze/{promotionId}")
    public AnalysisResponseDto analyze(
            @PathVariable Long promotionId,
            @RequestParam Long analysisJobId
    ) {
        AnalysisRequestDto aiRequest =
                promotionAnalysisService.buildAnalysisRequestForJob(promotionId, analysisJobId);

        AnalysisResponseDto response = aiService.callAi(aiRequest);

        promotionAnalysisService.saveAnalysisResult(promotionId, analysisJobId,response);

        String email = promotionAnalysisService.getPromotionOwnerEmail(promotionId);

        if (email != null && !email.isBlank()) {
            emailService.sendAnalysisResult(email, response);
        }

        return response;
    }
}