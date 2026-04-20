package com.hoppin.domain.Instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InstagramMeResponse(
        String id,

        @JsonProperty("user_id")
        String userId,

        String username,

        @JsonProperty("account_type")
        String accountType,

        @JsonProperty("media_count")
        Integer mediaCount
) {
}
