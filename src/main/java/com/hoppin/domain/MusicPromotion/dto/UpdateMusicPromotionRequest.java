package com.hoppin.domain.MusicPromotion.dto;

import java.time.LocalDate;
import java.util.List;

public record UpdateMusicPromotionRequest(
        String activityName,
        String songTitle,
        LocalDate releaseDate,
        List<UpdateStreamingLinkRequest> streamingLinks,
        String imageUrl,
        String shortDescription
) {
    public record UpdateStreamingLinkRequest(
            String redirectUrl,
            String url
    ) {
    }
}
