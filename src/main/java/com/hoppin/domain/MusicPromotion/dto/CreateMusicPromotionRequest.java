package com.hoppin.domain.MusicPromotion.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateMusicPromotionRequest(
        String activityName,
        String songTitle,
        LocalDate releaseDate,
        List<CreateStreamingLinkRequest> streamingLinks,
        String imageUrl,
        String shortDescription
) {
    public record CreateStreamingLinkRequest(String url) {}
}
