package com.hoppin.domain.PromotionStreamingClick.entity;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "promotion_streaming_click")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionStreamingClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streaming_click_id")
    private Long id;

    @Column(name = "visit_id", length = 50)
    private String visitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streaming_link_id", nullable = false)
    private PromotionStreamingLink streamingLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private MusicPromotion promotion;

    @Column(name = "streaming_code", nullable = false, length = 30)
    private String streamingCode;

    @Column(nullable = false, length = 255)
    private String domain;

    @Column(name = "clicked_url", nullable = false, length = 1000)
    private String clickedUrl;

    @Column(name = "destination_url", nullable = false, length = 1000)
    private String destinationUrl;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 500)
    private String referer;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    public PromotionStreamingClick(
            PromotionStreamingLink streamingLink,
            String visitId,
            String clickedUrl,
            String ipAddress,
            String userAgent,
            String referer
    ) {
        this.streamingLink = streamingLink;
        this.promotion = streamingLink.getPromotion();
        this.streamingCode = streamingLink.getStreamingCode();
        this.domain = streamingLink.getDomain();
        this.destinationUrl = streamingLink.getOriginalUrl();
        this.visitId = visitId;
        this.clickedUrl = clickedUrl;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.clickedAt = LocalDateTime.now();
    }
}
