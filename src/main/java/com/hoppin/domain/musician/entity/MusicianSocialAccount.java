package com.hoppin.domain.musician.entity;

import com.hoppin.domain.musician.enumtype.AuthProvider;
import com.hoppin.domain.musician.enumtype.SocialAccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "musician_social_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provider_provider_user_id",
                        columnNames = {"provider", "provider_user_id"}
                ),
                @UniqueConstraint(
                        name = "uk_musician_provider",
                        columnNames = {"musician_id", "provider"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicianSocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musician_id", nullable = false)
    private Musician musician;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(length = 255)
    private String email;

    @Column(length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SocialAccountStatus status;

    @Column(name = "linked_at", nullable = false)
    private LocalDateTime linkedAt;

    public MusicianSocialAccount(
            Musician musician,
            AuthProvider provider,
            String providerUserId,
            String email,
            String nickname
    ) {
        this.musician = musician;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.nickname = nickname;
        this.status = SocialAccountStatus.ACTIVE;
        this.linkedAt = LocalDateTime.now();
    }

    public void assignMusician(Musician musician) {
        this.musician = musician;
    }

    public void deactivate() {
        this.status = SocialAccountStatus.INACTIVE;
    }
}