package com.hoppin.infra.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionCardDto {
    private String title;
    private String reason;
    private String metric;
    private String example;
}