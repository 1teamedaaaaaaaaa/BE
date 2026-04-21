package com.hoppin.infra.ai.controller;

import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI", description = "AI 분석 및 피드백 관련 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService AiService;

    @Operation(
            summary = "AI 분석 요청",
            description = "사용자가 입력한 홍보 또는 분석 대상 데이터를 AI에 전달하여 분석 결과를 반환합니다."
    )
    @PostMapping("/analyze")
    public AnalysisResponseDto analyze(@RequestBody AnalysisRequestDto request) {
        return AiService.callAi(request);
    }
}