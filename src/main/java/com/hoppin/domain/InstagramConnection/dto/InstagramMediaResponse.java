package com.hoppin.domain.InstagramConnection.dto;

public record InstagramMediaResponse(
        String mediaId,
        String caption,
        String mediaType,
        String mediaUrl,
        String permalink,
        String thumbnailUrl,
        String timestamp,
        Long shareCount,
        Long profileVisitCount,
        Long reachCount
) {
}
