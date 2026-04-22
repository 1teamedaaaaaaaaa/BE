package com.hoppin.infra.ai.client;

import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;

public interface OpenAiClient {
    AnalysisResponseDto call(AnalysisRequestDto request);
}