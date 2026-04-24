package com.hoppin.domain.MusicPromotion.dto;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateMusicPromotionResponse(
        String trackingUrl
) {

    public static CreateMusicPromotionResponse from(
            String trackingUrl
    ) {
        return new CreateMusicPromotionResponse(
                trackingUrl
        );
    }
}

