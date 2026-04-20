package com.hoppin.ai.controller;

import com.hoppin.ai.dto.AnalysisRequestDto;
import com.hoppin.ai.dto.AnalysisResponseDto;
import com.hoppin.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService testAiService;

    @PostMapping("/analyze")
    public AnalysisResponseDto analyze(@RequestBody AnalysisRequestDto request) {
        return testAiService.callAi(request);
    }
}