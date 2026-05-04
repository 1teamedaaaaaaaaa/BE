package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisService;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.infra.ai.dto.request.AnalysisCreateRequest;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI", description = "AI 분석 및 피드백 관련 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final PromotionAnalysisService promotionAnalysisService;

    @Operation(
            summary = "AI 분석 요청",
            description = "JWT의 musicianId와 promotionId, 사용자가 선택한 날짜/고민/제약사항을 기준으로 데이터를 집계한 뒤 AI 분석 결과를 반환하고 DB에 저장합니다."
    )
    @PostMapping("/analyze/{promotionId}")
    public AnalysisResponseDto analyze(
            Authentication authentication,
            @PathVariable Long promotionId,
            @RequestBody AnalysisCreateRequest request
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        Long musicianId = musician.getId();

        AnalysisRequestDto aiRequest =
                promotionAnalysisService.buildAnalysisRequest(musicianId, promotionId, request);

        AnalysisResponseDto response = aiService.callAi(aiRequest);

        promotionAnalysisService.saveAnalysisResult(musicianId, promotionId, response);

        return response;
    }
}