package com.hoppin.domain.InstagramMediaInsight.infrastructure;

import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;
import com.hoppin.domain.InstagramMediaInsight.repository.MusicianInstagramInsightSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MusicianInstagramInsightSnapshotRepositoryJpaImpl
        implements MusicianInstagramInsightSnapshotRepository {

    private final MusicianInstagramInsightSnapshotJpaRepository jpaRepository;

    @Override
    public MusicianInstagramInsightSnapshot save(MusicianInstagramInsightSnapshot snapshot) {
        return jpaRepository.save(snapshot);
    }

    @Override
    public Optional<MusicianInstagramInsightSnapshot> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<MusicianInstagramInsightSnapshot> findByMusicianIdOrderByCreatedAtDesc(Long promotionId) {
        return jpaRepository.findByMusicianIdOrderByCreatedAtDesc(promotionId);
    }
}