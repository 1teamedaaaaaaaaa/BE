package com.hoppin.infra.ai.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisDto {
    private String bottleneckType;
    private String highlightSection;
    private int shareCount;
    private int profileVisitCount;
    private int linkClickCount;
    private String interpretation;
}