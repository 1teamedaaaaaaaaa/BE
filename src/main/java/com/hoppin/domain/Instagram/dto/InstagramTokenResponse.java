package com.hoppin.domain.Instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InstagramTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("permissions")
        List<String> permissions
) {
}
