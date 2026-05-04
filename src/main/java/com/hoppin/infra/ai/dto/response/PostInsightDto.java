package com.hoppin.infra.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInsightDto {

    private String topPostPattern;
    private String lowPostPattern;
    private String suggestion;
}