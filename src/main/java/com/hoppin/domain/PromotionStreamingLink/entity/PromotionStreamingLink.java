package com.hoppin.domain.PromotionStreamingLink.entity;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "promotion_streaming_link")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionStreamingLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streaming_link_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private MusicPromotion promotion;

    @Column(name = "streaming_code", nullable = false, unique = true, length = 30)
    private String streamingCode;

    @Column(name = "original_url", nullable = false, length = 1000)
    private String originalUrl;

    @Column(nullable = false, length = 255)
    private String domain;

    @Column(name = "redirect_url", nullable = false, length = 1000)
    private String redirectUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PromotionStreamingLink(
            MusicPromotion promotion,
            String streamingCode,
            String originalUrl,
            String domain,
            String redirectUrl,
            int displayOrder
    ) {
        this.promotion = promotion;
        this.streamingCode = streamingCode;
        this.originalUrl = originalUrl;
        this.domain = domain;
        this.redirectUrl = redirectUrl;
        this.displayOrder = displayOrder;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}
