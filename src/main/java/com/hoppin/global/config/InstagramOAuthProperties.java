package com.hoppin.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "instagram.oauth")
public record InstagramOAuthProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String tokenUrl,
        String longLivedTokenUrl,
        String refreshTokenUrl
) {
}
