package com.hoppin.domain.MusicPromotion.dto;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateMusicPromotionResponse(
        Long promotionId,
        String trackingCode,
        String trackingUrl,
        String detailUrl,
        String activityName,
        String instagramAccount,
        String songTitle,
        LocalDate releaseDate,
        String streamingUrl,
        String imageUrl,
        String shortDescription,
        LocalDateTime createdAt
) {

    public static CreateMusicPromotionResponse from(
            MusicPromotion promotion,
            PromotionTrackingLink trackingLink,
            String detailUrl
    ) {
        return new CreateMusicPromotionResponse(
                promotion.getId(),
                trackingLink.getTrackingCode(),
                trackingLink.getTrackingUrl(),
                detailUrl,
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

