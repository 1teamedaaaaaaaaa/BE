package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisService;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/ai")
public class InternalAiAnalysisController {

    private final PromotionAnalysisService promotionAnalysisService;
    private final AiService aiService;

    @PostMapping("/analyze/{promotionId}")
    public AnalysisResponseDto analyze(
            @PathVariable Long promotionId,
            @RequestParam Long analysisJobId
    ) {
        AnalysisRequestDto aiRequest =
                promotionAnalysisService.buildAnalysisRequest(analysisJobId, promotionId);

        AnalysisResponseDto response = aiService.callAi(aiRequest);

        promotionAnalysisService.saveAnalysisResult(promotionId, response);

        return response;
    }
}
