package com.hoppin.domain.InstagramMediaInsight.entity;

import com.hoppin.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "instagram_media_insights",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_snapshot_media",
                        columnNames = {"snapshot_id", "media_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstagramMediaInsight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 동기화 스냅샷에 속한 게시물 지표인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private MusicianInstagramInsightSnapshot snapshot;

    @Column(name = "media_id", nullable = false)
    private String mediaId;

    @Column(columnDefinition = "TEXT")
    private String caption;

    private String mediaType;

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(columnDefinition = "TEXT")
    private String permalink;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    private String timestamp;

    private Long shareCount;

    private Long profileVisitCount;

    private Long reachCount;

    public InstagramMediaInsight(
            MusicianInstagramInsightSnapshot snapshot,
            String mediaId,
            String caption,
            String mediaType,
            String mediaUrl,
            String permalink,
            String thumbnailUrl,
            String timestamp,
            Long shareCount,
            Long profileVisitCount,
            Long reachCount
    ) {
        this.snapshot = snapshot;
        this.mediaId = mediaId;
        this.caption = caption;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.permalink = permalink;
        this.thumbnailUrl = thumbnailUrl;
        this.timestamp = timestamp;
        this.shareCount = shareCount;
        this.profileVisitCount = profileVisitCount;
        this.reachCount = reachCount;
    }
}