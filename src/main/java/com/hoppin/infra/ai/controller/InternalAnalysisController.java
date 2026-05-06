package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisJobService;
import com.hoppin.global.response.ApiResponse;
import com.hoppin.global.security.InternalApiKeyValidator;
import com.hoppin.infra.ai.dto.request.AnalysisCrawlerResultRequest;
import com.hoppin.infra.ai.dto.request.AnalysisJobStatusUpdateRequest;
import com.hoppin.infra.ai.dto.response.AnalysisJobContextResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/analysis-jobs")
@RequiredArgsConstructor
public class InternalAnalysisController {

    private final PromotionAnalysisJobService promotionAnalysisJobService;
    private final InternalApiKeyValidator internalApiKeyValidator;

    @GetMapping("/{analysisJobId}/context")
    public ApiResponse<AnalysisJobContextResponse> getAnalysisJobContext(
            @RequestHeader("X-Internal-Api-Key") String internalApiKey,
            @PathVariable Long analysisJobId
    ) {
        internalApiKeyValidator.validate(internalApiKey);

        AnalysisJobContextResponse response = promotionAnalysisJobService.getJobContext(analysisJobId);
        return ApiResponse.success(response, "분석 작업 컨텍스트를 조회했습니다.");
    }

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
