package com.hoppin.domain.Instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InstagramLongLivedTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
}
