package com.hoppin.infra.ai.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnalysisCreateRequest {

    private LocalDate sinceDate;

    private String mainPainPoint;

    private String mainResourceConstraint;
}