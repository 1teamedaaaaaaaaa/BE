package com.hoppin.domain.musician.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import com.hoppin.domain.musician.enumtype.MusicianStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "musician")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Musician extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musician_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 255)
    private String hashedPassword;

    @Column(name = "local_login_enabled", nullable = false)
    private boolean localLoginEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MusicianStatus status;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @OneToMany(mappedBy = "musician", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MusicianSocialAccount> socialAccounts = new ArrayList<>();

    public Musician(String name, String email, String password, boolean localLoginEnabled) {
        this.name = name;
        this.email = email;
        this.hashedPassword = password;
        this.localLoginEnabled = localLoginEnabled;
        this.status = MusicianStatus.ACTIVE;
        this.withdrawnAt = null;
    }

    public Musician(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addSocialAccount(MusicianSocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
        socialAccount.assignMusician(this);
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void withdraw() {
        if (this.status == MusicianStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }
        this.status = MusicianStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.status == MusicianStatus.WITHDRAWN;
    }
}