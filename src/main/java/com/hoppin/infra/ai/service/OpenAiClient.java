package com.hoppin.infra.ai.service;

import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;

public interface OpenAiClient {
    AnalysisResponseDto call(AnalysisRequestDto request);
}