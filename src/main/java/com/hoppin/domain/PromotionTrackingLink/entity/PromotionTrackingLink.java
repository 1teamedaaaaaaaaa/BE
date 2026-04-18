package com.hoppin.domain.PromotionTrackingLink.entity;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "promotion_tracking_link")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionTrackingLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_link_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private MusicPromotion promotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PromotionChannel channel;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 30)
    private String trackingCode;

    @Column(name = "tracking_url", nullable = false, length = 500)
    private String trackingUrl;

    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PromotionTrackingLink(
            MusicPromotion promotion,
            PromotionChannel channel,
            String trackingCode,
            String trackingUrl,
            String targetUrl
    ) {
        this.promotion = promotion;
        this.channel = channel;
        this.trackingCode = trackingCode;
        this.trackingUrl = trackingUrl;
        this.targetUrl = targetUrl;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
    }
}

