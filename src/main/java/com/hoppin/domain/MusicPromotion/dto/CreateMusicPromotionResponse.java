package com.hoppin.domain.MusicPromotion.dto;

public record CreateMusicPromotionResponse(
        Long promotionId
) {

    public static CreateMusicPromotionResponse from(
            Long promotionId
    ) {
        return new CreateMusicPromotionResponse(
                promotionId
        );
    }
}

