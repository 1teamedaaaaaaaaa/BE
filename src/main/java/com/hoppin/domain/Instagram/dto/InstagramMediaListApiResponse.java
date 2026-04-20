package com.hoppin.domain.Instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InstagramMediaListApiResponse(
        List<InstagramMediaItem> data
) {

    public record InstagramMediaItem(
            String id,

            String caption,

            @JsonProperty("media_type")
            String mediaType,

            @JsonProperty("media_url")
            String mediaUrl,

            String permalink,

            @JsonProperty("thumbnail_url")
            String thumbnailUrl,

            String timestamp
    ) {
    }
}
