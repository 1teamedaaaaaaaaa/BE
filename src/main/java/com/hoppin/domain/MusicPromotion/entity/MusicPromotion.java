package com.hoppin.domain.MusicPromotion.entity;

import com.hoppin.domain.musician.entity.Musician;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "music_promotion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musician_id", nullable = false)
    private Musician musician;

    @Column(name = "activity_name", nullable = false, length = 100)
    private String activityName;

    @Column(name = "instagram_account", nullable = false, length = 100)
    private String instagramAccount;

    @Column(name = "song_title", nullable = false, length = 100)
    private String songTitle;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "streaming_url", nullable = false, length = 500)
    private String streamingUrl;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "short_description", nullable = false, length = 255)
    private String shortDescription;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MusicPromotion(
            Musician musician,
            String activityName,
            String instagramAccount,
            String songTitle,
            LocalDate releaseDate,
            String streamingUrl,
            String imageUrl,
            String shortDescription
    ) {
        this.musician = musician;
        this.activityName = activityName;
        this.instagramAccount = instagramAccount;
        this.songTitle = songTitle;
        this.releaseDate = releaseDate;
        this.streamingUrl = streamingUrl;
        this.imageUrl = imageUrl;
        this.shortDescription = shortDescription;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
