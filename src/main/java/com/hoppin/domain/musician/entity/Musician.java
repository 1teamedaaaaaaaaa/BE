package com.hoppin.domain.musician.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @OneToMany(mappedBy = "musician", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MusicianSocialAccount> socialAccounts = new ArrayList<>();

    public void addSocialAccount(MusicianSocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
        socialAccount.assignMusician(this);
    }

    public Musician(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }
}