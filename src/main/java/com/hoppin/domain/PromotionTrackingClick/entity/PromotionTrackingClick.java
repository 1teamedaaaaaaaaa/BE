package com.hoppin.domain.PromotionTrackingClick.entity;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "promotion_tracking_click")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionTrackingClick extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_link_id", nullable = false)
    private PromotionTrackingLink trackingLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private MusicPromotion promotion;

    @Column(name = "visit_id", length = 50)
    private String visitId;

    @Column(name = "tracking_code", nullable = false, length = 30)
    private String trackingCode;

    @Column(name = "clicked_url", nullable = false, length = 1000)
    private String clickedUrl;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 500)
    private String referer;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    public PromotionTrackingClick(
            PromotionTrackingLink trackingLink,
            String visitId,
            String clickedUrl,
            String ipAddress,
            String userAgent,
            String referer
    ) {
        this.trackingLink = trackingLink;
        this.visitId = visitId;
        this.promotion = trackingLink.getPromotion();
        this.trackingCode = trackingLink.getTrackingCode();
        this.clickedUrl = clickedUrl;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.clickedAt = LocalDateTime.now();
    }
}
