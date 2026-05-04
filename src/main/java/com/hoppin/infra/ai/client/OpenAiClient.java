package com.hoppin.infra.ai.client;

import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;

public interface OpenAiClient {
    AnalysisResponseDto call(AnalysisRequestDto request);
}