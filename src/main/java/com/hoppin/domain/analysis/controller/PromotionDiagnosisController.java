package com.hoppin.domain.analysis.controller;

import com.hoppin.domain.analysis.dto.response.PromotionDiagnosisDetailResponse;
import com.hoppin.domain.analysis.service.PromotionDiagnosisQueryService;
import com.hoppin.domain.musician.entity.Musician;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promotion Diagnosis", description = "음악 홍보 AI 진단 결과 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/music-promotions")
public class PromotionDiagnosisController {

    private final PromotionDiagnosisQueryService promotionDiagnosisQueryService;

    @Operation(
            summary = "AI 진단 결과 상세 조회",
            description = """
                    사용자가 선택한 특정 AI 진단 결과를 조회합니다.

                    이 API는 피드/프로필 진단 상세 페이지에서 사용됩니다.
                    최신 진단이 아니라, 사용자가 클릭한 diagnosisId에 해당하는 진단 결과를 반환합니다.

                    응답에는 다음 정보가 포함됩니다.
                    - headline: AI가 요약한 한 문장 핵심 진단
                    - periodLabel: 분석 기간. 사용자가 선택한 sinceDate부터 진단 완료일까지의 기간
                    - summaryMetrics: 상단 카드에 표시할 핵심 지표 3개
                      - followerEngagementRate: 팔로워 대비 게시글 반응률
                      - promoClickRateByEngagement: 반응 대비 홍보 링크 클릭률
                      - streamingClickRateByPromoClick: 홍보 링크 클릭 대비 스트리밍 링크 클릭률
                    - diagnosis.highlightSection: 병목 구간
                    - action: 7일 내 실행할 액션 카드

                    JWT 인증이 필요하며, 본인의 홍보 진단 결과만 조회할 수 있습니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{promotionId}/diagnoses/{diagnosisId}")
    public PromotionDiagnosisDetailResponse getDiagnosisDetail(
            Authentication authentication,

            @Parameter(description = "음악 홍보 ID", example = "102", required = true)
            @PathVariable Long promotionId,

            @Parameter(description = "조회할 AI 진단 결과 ID", example = "15", required = true)
            @PathVariable Long diagnosisId
    ) {
        Musician musician = (Musician) authentication.getPrincipal();

        return promotionDiagnosisQueryService.getDiagnosisDetail(
                musician.getId(),
                promotionId,
                diagnosisId
        );
    }
}