package com.hoppin.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisRequestDto {
    private int contentCountIn28Days;
    private String mainPainPoint;
    private String mainResourceConstraint;
    private String promoLink;
    private int shareCount;
    private int profileVisitCount;
    private int linkClickCount;
    private String periodLabel;
}