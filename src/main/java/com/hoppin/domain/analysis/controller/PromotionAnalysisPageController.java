package com.hoppin.domain.analysis.controller;

import com.hoppin.domain.analysis.dto.PromotionAnalysisPageResponse;
import com.hoppin.domain.analysis.service.PromotionAnalysisPageQueryService;
import com.hoppin.domain.musician.entity.Musician;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promotion Analysis Page", description = "홍보 페이지 분석 화면 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/music-promotions")
public class PromotionAnalysisPageController {

    private final PromotionAnalysisPageQueryService promotionAnalysisPageQueryService;

    @Operation(
            summary = "홍보 페이지 분석 화면 조회",
            description = """
                    특정 음악 홍보의 분석 화면 데이터를 조회합니다.

                
                    - 진단 상태
                      - RUNNING
                      - COMPLETED
                      - NOT STARTED
                    - 진단 완료 시 최신 진단 카드 정보

                    진단 카드를 클릭하면 diagnosisId를 이용해
                    GET /api/music-promotions/{promotionId}/diagnoses/{diagnosisId}
                    API를 호출하면 됩니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{promotionId}/analysis-page")
    public PromotionAnalysisPageResponse getAnalysisPage(
            Authentication authentication,

            @Parameter(description = "음악 홍보 ID", example = "130", required = true)
            @PathVariable Long promotionId
    ) {
        Musician musician = (Musician) authentication.getPrincipal();

        return promotionAnalysisPageQueryService.getAnalysisPage(
                musician.getId(),
                promotionId
        );
    }
}