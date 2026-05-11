package com.hoppin.infra.ai.controller;

import com.hoppin.domain.analysis.service.PromotionAnalysisService;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import com.hoppin.infra.mail.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Internal AI",
        description = "n8n 및 서버 내부 워크플로우에서 사용하는 AI 분석 API"
)
@RestController
@RequestMapping("/api/internal/ai")
@RequiredArgsConstructor
public class InternalAiController {

    private final AiService aiService;
    private final PromotionAnalysisService promotionAnalysisService;
    private final EmailService emailService;

    @Operation(
            summary = "내부 AI 분석 실행",
            description = """
                    n8n 또는 서버 내부 워크플로우에서 호출하는 AI 분석 API입니다.

                    promotionId와 analysisJobId를 기준으로 해당 분석 작업 데이터를 조회한 뒤,
                    AI 분석을 실행하고 결과를 DB에 저장합니다.

                    분석 완료 후 홍보 소유자의 이메일이 존재하면 분석 결과 안내 메일을 발송합니다.

                    이 API는 사용자가 직접 호출하는 일반 API가 아니라,
                    크롤링 완료 이후 내부 자동화 플로우에서 호출하는 용도입니다.

                    처리 흐름:
                    1. promotionId와 analysisJobId로 AI 분석 요청 데이터 생성
                    2. AI API 호출
                    3. AI 분석 결과 DB 저장
                    4. 홍보 소유자 이메일 조회
                    5. 이메일이 존재하면 분석 결과 메일 발송
                    6. AI 분석 응답 반환
                    """
    )
    @PostMapping("/analyze/{promotionId}")
    public AnalysisResponseDto analyze(
            @PathVariable Long promotionId,
            @RequestParam Long analysisJobId
    ) {
        AnalysisRequestDto aiRequest =
                promotionAnalysisService.buildAnalysisRequestForJob(promotionId, analysisJobId);

        AnalysisResponseDto response = aiService.callAi(aiRequest);

        promotionAnalysisService.saveAnalysisResult(promotionId, analysisJobId, response);

        String email = promotionAnalysisService.getPromotionOwnerEmail(promotionId);

        if (email != null && !email.isBlank()) {
            emailService.sendAnalysisResult(email, response);
        }

        return response;
    }
}