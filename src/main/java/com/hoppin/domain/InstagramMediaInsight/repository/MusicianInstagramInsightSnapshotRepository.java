package com.hoppin.domain.InstagramMediaInsight.repository;


import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;

import java.util.List;
import java.util.Optional;

public interface MusicianInstagramInsightSnapshotRepository {

    MusicianInstagramInsightSnapshot save(MusicianInstagramInsightSnapshot snapshot);

    Optional<MusicianInstagramInsightSnapshot> findById(Long id);

    List<MusicianInstagramInsightSnapshot> findByMusicianIdOrderByCreatedAtDesc(Long promotionId);
}