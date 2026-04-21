package com.hoppin.infra.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalysisResponseDto {

    private String headline;
    private DiagnosisDto diagnosis;
    private List<ActionCardDto> actions;
}