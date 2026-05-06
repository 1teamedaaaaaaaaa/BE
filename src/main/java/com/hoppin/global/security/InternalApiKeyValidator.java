package com.hoppin.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalApiKeyValidator {

    private final String internalApiKey;

    public InternalApiKeyValidator(@Value("${app.internal-api-key}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    public void validate(String headerValue) {
        if (headerValue == null || headerValue.isBlank() || !internalApiKey.equals(headerValue)) {
            throw new IllegalArgumentException("유효하지 않은 내부 API 키입니다.");
        }
    }
}
