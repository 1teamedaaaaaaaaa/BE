package com.hoppin.infra.crawling.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstagramProfileValidateResponse {

    private boolean valid;

    /**
     * AVAILABLE
     * INVALID_USERNAME
     * NOT_FOUND
     * PRIVATE_PROFILE
     * LOGIN_REQUIRED
     * VALIDATION_FAILED
     */
    private String status;

    private String message;
}