package com.hoppin.infra.crawling.controller;

import com.hoppin.infra.crawling.service.PromotionAnalysisJobService;
import com.hoppin.global.response.ApiResponse;
import com.hoppin.global.security.InternalApiKeyValidator;
import com.hoppin.infra.crawling.dto.request.AnalysisCrawlerResultRequest;
import com.hoppin.infra.crawling.dto.request.AnalysisJobStatusUpdateRequest;
import com.hoppin.infra.crawling.dto.response.AnalysisJobContextResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal Analysis", description = "n8n 및 내부 워크플로우용 분석 작업 API")
@RestController
@RequestMapping("/api/internal/analysis-jobs")
@RequiredArgsConstructor
public class InternalAnalysisController {

    private final PromotionAnalysisJobService promotionAnalysisJobService;
    private final InternalApiKeyValidator internalApiKeyValidator;

    @Operation(
            summary = "분석 작업 컨텍스트 조회",
            description = """
                    n8n 또는 내부 워크플로우에서 분석 작업을 시작하기 전에 필요한 컨텍스트를 조회합니다.

                    반환 값에는 promotionId, musicianId, instagramUsername, sinceDate,
                    mainPainPoint, mainResourceConstraint, trackingUrl, releaseDate가 포함됩니다.
                    """
    )
    @GetMapping("/{analysisJobId}/context")
    public ApiResponse<AnalysisJobContextResponse> getAnalysisJobContext(
            @RequestHeader("X-Internal-Api-Key") String internalApiKey,
            @PathVariable Long analysisJobId
    ) {
        internalApiKeyValidator.validate(internalApiKey);

        AnalysisJobContextResponse response = promotionAnalysisJobService.getJobContext(analysisJobId);
        return ApiResponse.success(response, "분석 작업 컨텍스트를 조회했습니다.");
    }

    @Operation(
            summary = "크롤링 결과 저장",
            description = """
                    n8n 또는 내부 크롤러가 수집한 게시물 목록과 집계 데이터를 저장합니다.

                    저장 후 기존 크롤링 결과를 교체하고,
                    contentCount, followerCount, totalLikeCount, totalCommentCount를 갱신한 뒤
                    분석 작업 상태를 COMPLETED로 변경합니다.
                    """
    )
    @PostMapping("/{analysisJobId}/crawler-result")
    public ApiResponse<Void> saveCrawlerResult(
            @RequestHeader("X-Internal-Api-Key") String internalApiKey,
            @PathVariable Long analysisJobId,
            @RequestBody AnalysisCrawlerResultRequest request
    ) {
        internalApiKeyValidator.validate(internalApiKey);

        promotionAnalysisJobService.saveCrawlerResult(analysisJobId, request);
        return ApiResponse.success(null, "크롤링 결과를 저장했습니다.");
    }

    @Operation(
            summary = "분석 작업 상태 변경",
            description = """
                    내부 워크플로우가 분석 작업 상태를 변경할 때 사용하는 API입니다.

                    지원하는 상태값은 다음과 같습니다.
                    - RUNNING: 크롤링 또는 분석 작업 시작
                    - FAILED: 작업 실패
                    """
    )
    @PatchMapping("/{analysisJobId}/status")
    public ApiResponse<Void> updateAnalysisJobStatus(
            @RequestHeader("X-Internal-Api-Key") String internalApiKey,
            @PathVariable Long analysisJobId,
            @RequestBody AnalysisJobStatusUpdateRequest request
    ) {
        internalApiKeyValidator.validate(internalApiKey);

        if ("RUNNING".equalsIgnoreCase(request.getStatus())) {
            promotionAnalysisJobService.markJobRunning(analysisJobId);
            return ApiResponse.success(null, "분석 작업 상태를 RUNNING으로 변경했습니다.");
        }

        if ("FAILED".equalsIgnoreCase(request.getStatus())) {
            promotionAnalysisJobService.markJobFailed(analysisJobId, request.getErrorMessage());
            return ApiResponse.success(null, "분석 작업 상태를 FAILED로 변경했습니다.");
        }

        throw new IllegalArgumentException("지원하지 않는 상태값입니다.");
    }
}
