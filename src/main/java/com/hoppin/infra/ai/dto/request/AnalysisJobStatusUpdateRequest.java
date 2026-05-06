package com.hoppin.infra.ai.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisJobStatusUpdateRequest {

    private String status;
    private String errorMessage;
}
