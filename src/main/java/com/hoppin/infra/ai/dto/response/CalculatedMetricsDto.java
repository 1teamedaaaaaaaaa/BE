package com.hoppin.infra.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatedMetricsDto {

    private double avgReachPerPost;
    private double avgSharePerPost;
    private double avgProfileVisitPerPost;

    private double shareRateByReach;
    private double profileVisitRateByReach;
    private double linkClickRateByProfileVisit;
    private double linkClickRateByReach;
}