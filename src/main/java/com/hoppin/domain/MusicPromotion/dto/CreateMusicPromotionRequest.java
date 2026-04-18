package com.hoppin.domain.MusicPromotion.dto;

import java.time.LocalDate;

public record CreateMusicPromotionRequest(
        String activityName,
        String instagramAccount,
        String songTitle,
        LocalDate releaseDate,
        String streamingUrl,
        String imageUrl,
        String shortDescription
) {
}
