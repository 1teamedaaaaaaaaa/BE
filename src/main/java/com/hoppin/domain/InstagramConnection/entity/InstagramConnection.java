package com.hoppin.domain.InstagramConnection.entity;

import com.hoppin.domain.Musician.entity.Musician;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "instagram_connection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstagramConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instagram_connection_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musician_id", nullable = false)
    private Musician musician;

    @Column(name = "instagram_account_id", nullable = false, length = 100)
    private String instagramAccountId;

    @Column(name = "instagram_username", nullable = false, length = 100)
    private String instagramUsername;

    @Column(name = "access_token", nullable = false, length = 4000)
    private String accessToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "connected_at", nullable = false)
    private LocalDateTime connectedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InstagramConnection(
            Musician musician,
            String instagramAccountId,
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        this.musician = musician;
        this.instagramAccountId = instagramAccountId;
        this.instagramUsername = instagramUsername;
        this.accessToken = accessToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.connectedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateToken(String accessToken, LocalDateTime tokenExpiresAt) {
        this.accessToken = accessToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfileAndToken(
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        this.instagramUsername = instagramUsername;
        this.accessToken = accessToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.updatedAt = LocalDateTime.now();
    }
}
