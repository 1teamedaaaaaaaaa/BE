package com.hoppin.domain.InstagramMediaInsight.infrastructure;

import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MusicianInstagramInsightSnapshotJpaRepository
        extends JpaRepository<MusicianInstagramInsightSnapshot, Long> {

    List<MusicianInstagramInsightSnapshot> findByMusicianIdOrderByCreatedAtDesc(Long promotionId);

    Optional<MusicianInstagramInsightSnapshot> findTopByMusicianIdOrderByCreatedAtDesc(Long musicianId);
}