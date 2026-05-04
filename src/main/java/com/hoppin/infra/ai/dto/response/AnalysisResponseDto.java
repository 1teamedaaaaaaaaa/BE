package com.hoppin.infra.ai.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalysisResponseDto {

    private String headline;
    private DiagnosisDto diagnosis;

    private CalculatedMetricsDto calculatedMetrics;
    private ChannelInsightDto channelInsight;
    private PostInsightDto postInsight;

    private List<ActionCardDto> actions;
}