package com.hoppin.domain.MusicPromotion.dto;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MusicPromotionDetailResponse(
        Long promotionId,
        String trackingCode,
        String trackingUrl,
        String activityName,
        String instagramAccount,
        String songTitle,
        LocalDate releaseDate,
        String streamingUrl,
        String imageUrl,
        String shortDescription,
        LocalDateTime createdAt
) {

    public static MusicPromotionDetailResponse from(
            MusicPromotion promotion,
            PromotionTrackingLink trackingLink
    ) {
        return new MusicPromotionDetailResponse(
                promotion.getId(),
                trackingLink.getTrackingCode(),
                trackingLink.getTrackingUrl(),
                promotion.getActivityName(),
                promotion.getInstagramAccount(),
                promotion.getSongTitle(),
                promotion.getReleaseDate(),
                promotion.getStreamingUrl(),
                promotion.getImageUrl(),
                promotion.getShortDescription(),
                promotion.getCreatedAt()
        );
    }
}
