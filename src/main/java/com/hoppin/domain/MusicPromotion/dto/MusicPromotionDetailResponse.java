package com.hoppin.domain.MusicPromotion.dto;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MusicPromotionDetailResponse(
        Long promotionId,
        String activityName,
        String songTitle,
        LocalDate releaseDate,
        String imageUrl,
        String shortDescription,
        LocalDateTime createdAt,
        String trackingUrl,
        List<StreamingLinkResponse> streamingLinks
) {

    public static MusicPromotionDetailResponse from(
            MusicPromotion promotion,
            String trackingUrl,
            List<PromotionStreamingLink> streamingLinks
    ) {
        return new MusicPromotionDetailResponse(
                promotion.getId(),
                promotion.getActivityName(),
                promotion.getSongTitle(),
                promotion.getReleaseDate(),
                promotion.getImageUrl(),
                promotion.getShortDescription(),
                promotion.getCreatedAt(),
                trackingUrl,
                streamingLinks.stream()
                        .map(StreamingLinkResponse::from)
                        .toList()
        );
    }

    public record StreamingLinkResponse(
            String url,
            String clickUrl,
            Integer displayOrder
    ) {

        public static StreamingLinkResponse from(PromotionStreamingLink streamingLink) {
            return new StreamingLinkResponse(
                    streamingLink.getOriginalUrl(),
                    streamingLink.getRedirectUrl(),
                    streamingLink.getDisplayOrder()
            );
        }
    }
}
