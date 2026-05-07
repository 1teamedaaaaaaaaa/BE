package com.hoppin.infra.crawling.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisJobStatusUpdateRequest {

    private String status;
    private String errorMessage;
}
