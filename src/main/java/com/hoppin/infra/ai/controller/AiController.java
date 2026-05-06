package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisJobService;
import com.hoppin.domain.analysis.service.PromotionAnalysisService;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.infra.ai.dto.request.AnalysisCreateRequest;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisCrawlerResultResponse;
import com.hoppin.infra.ai.dto.response.AnalysisJobCreateResponse;
import com.hoppin.infra.ai.dto.response.AnalysisJobStatusResponse;
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
    private final PromotionAnalysisJobService promotionAnalysisJobService;

    @Operation(
            summary = "AI 분석 요청",
            description = "JWT의 musicianId와 promotionId를 기준으로 최신 크롤링 작업 데이터를 조회한 뒤 AI 분석 결과를 반환하고 DB에 저장합니다."
    )
    @PostMapping("/analyze/{promotionId}")
    public AnalysisResponseDto analyze(
            Authentication authentication,
            @PathVariable Long promotionId
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        Long musicianId = musician.getId();

        AnalysisRequestDto aiRequest =
                promotionAnalysisService.buildLatestAnalysisRequest(musicianId, promotionId);

        AnalysisResponseDto response = aiService.callAi(aiRequest);

        promotionAnalysisService.saveAnalysisResult(musicianId, promotionId, response);

        return response;
    }

    @Operation(
            summary = "크롤링 기반 AI 분석 작업 시작",
            description = "인스타 공개 데이터를 수집하는 비동기 분석 작업을 생성하고 작업 ID를 반환합니다."
    )
    @PostMapping("/analysis-jobs/{promotionId}")
    public AnalysisJobCreateResponse createAnalysisJob(
            Authentication authentication,
            @PathVariable Long promotionId,
            @RequestBody AnalysisCreateRequest request
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        return promotionAnalysisJobService.createJob(musician.getId(), promotionId, request);
    }

    @Operation(
            summary = "크롤링 기반 AI 분석 작업 상태 조회",
            description = "비동기 분석 작업의 현재 상태를 조회합니다."
    )
    @GetMapping("/analysis-jobs/{analysisJobId}")
    public AnalysisJobStatusResponse getAnalysisJobStatus(
            Authentication authentication,
            @PathVariable Long analysisJobId
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        return promotionAnalysisJobService.getJobStatus(musician.getId(), analysisJobId);
    }

    @Operation(
            summary = "크롤링 기반 AI 분석 수집 결과 조회",
            description = "비동기 분석 작업에 저장된 게시물별 크롤링 결과와 집계값을 조회합니다."
    )
    @GetMapping("/analysis-jobs/{analysisJobId}/crawler-result")
    public AnalysisCrawlerResultResponse getCrawlerResult(
            Authentication authentication,
            @PathVariable Long analysisJobId
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        return promotionAnalysisJobService.getCrawlerResult(musician.getId(), analysisJobId);
    }
}