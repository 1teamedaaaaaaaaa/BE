package com.hoppin.domain.InstagramMediaInsight.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import com.hoppin.domain.musician.entity.Musician;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "musician_instagram_insight_snapshots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicianInstagramInsightSnapshot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 뮤지션의 인스타 지표 스냅샷인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musician_id", nullable = false)
    private Musician musician;

    private Long totalShareCount;

    private Long totalProfileVisitCount;

    private Long totalReachCount;

    private Integer mediaCount;

    public MusicianInstagramInsightSnapshot(
            Musician musician,
            Long totalShareCount,
            Long totalProfileVisitCount,
            Long totalReachCount,
            Integer mediaCount
    ) {
        this.musician = musician;
        this.totalShareCount = totalShareCount;
        this.totalProfileVisitCount = totalProfileVisitCount;
        this.totalReachCount = totalReachCount;
        this.mediaCount = mediaCount;
    }
}