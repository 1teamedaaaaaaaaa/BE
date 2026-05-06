package com.hoppin.domain.MusicPromotion.dto;

public record CreateMusicPromotionResponse(
        String trackingUrl,
        Long promotionId
) {

    public static CreateMusicPromotionResponse from(
            String trackingUrl,
            Long promotionId
    ) {
        return new CreateMusicPromotionResponse(
                trackingUrl,
                promotionId
        );
    }
}

