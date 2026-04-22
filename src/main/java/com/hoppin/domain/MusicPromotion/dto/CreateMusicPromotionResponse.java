package com.hoppin.domain.MusicPromotion.dto;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import java.time.LocalDate;
import java.time.LocalDateTime;

// TODO: 나중에 promotionId/detailUrl/trackingUrl 이 3개로만 응답 변수들 만들기.
// 다른 변수들은 필요없음.
public record CreateMusicPromotionResponse(
        Long promotionId,
        String trackingCode,
        String trackingUrl,
        String detailUrl,
        String activityName,
        String instagramAccount,
        String songTitle,
        LocalDate releaseDate,
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
                promotion.getImageUrl(),
                promotion.getShortDescription(),
                promotion.getCreatedAt()
        );
    }
}

