package com.hoppin.infra.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelInsightDto {

    private String bestChannel;
    private double bestChannelClickRate;
    private String summary;
}