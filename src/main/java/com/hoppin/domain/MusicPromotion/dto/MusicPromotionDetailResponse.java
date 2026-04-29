package com.hoppin.domain.MusicPromotion.dto;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MusicPromotionDetailResponse(
        Long promotionId,
        String trackingCode,
        String trackingUrl,
        String activityName,
        String songTitle,
        LocalDate releaseDate,
        String imageUrl,
        String shortDescription,
        LocalDateTime createdAt,
        List<StreamingLinkResponse> streamingLinks
) {

    public static MusicPromotionDetailResponse from(
            MusicPromotion promotion,
            PromotionTrackingLink trackingLink,
            List<PromotionStreamingLink> streamingLinks
    ) {
        return new MusicPromotionDetailResponse(
                promotion.getId(),
                trackingLink.getTrackingCode(),
                trackingLink.getTrackingUrl(),
                promotion.getActivityName(),
                promotion.getSongTitle(),
                promotion.getReleaseDate(),
                promotion.getImageUrl(),
                promotion.getShortDescription(),
                promotion.getCreatedAt(),
                streamingLinks.stream()
                        .map(StreamingLinkResponse::from)
                        .toList()
        );
    }

    public record StreamingLinkResponse(
            String streamingCode,
            String domain,
            String redirectUrl,
            Integer displayOrder
    ) {

        public static StreamingLinkResponse from(PromotionStreamingLink streamingLink) {
            return new StreamingLinkResponse(
                    streamingLink.getStreamingCode(),
                    streamingLink.getDomain(),
                    streamingLink.getRedirectUrl(),
                    streamingLink.getDisplayOrder()
            );
        }
    }
}