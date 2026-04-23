package com.hoppin.domain.MusicPromotion.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateMusicPromotionRequest(
        String activityName,
        String instagramAccount,
        String songTitle,
        LocalDate releaseDate,
        List<StreamingLinkRequest> streamingLinks,
        String imageUrl,
        String shortDescription
) {
    public record StreamingLinkRequest(String url) {}
}
