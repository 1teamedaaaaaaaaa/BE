package com.hoppin.ai.service;

import com.hoppin.ai.dto.AnalysisRequestDto;
import com.hoppin.ai.dto.AnalysisResponseDto;

public interface OpenAiClient {
    AnalysisResponseDto call(AnalysisRequestDto request);
}