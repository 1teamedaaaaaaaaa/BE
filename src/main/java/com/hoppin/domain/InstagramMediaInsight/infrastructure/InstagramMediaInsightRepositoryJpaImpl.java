package com.hoppin.domain.InstagramMediaInsight.infrastructure;

import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;
import com.hoppin.domain.InstagramMediaInsight.repository.InstagramMediaInsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InstagramMediaInsightRepositoryJpaImpl implements InstagramMediaInsightRepository {

    private final InstagramMediaInsightJpaRepository jpaRepository;

    @Override
    public InstagramMediaInsight save(InstagramMediaInsight insight) {
        return jpaRepository.save(insight);
    }

    @Override
    public List<InstagramMediaInsight> saveAll(List<InstagramMediaInsight> insights) {
        return jpaRepository.saveAll(insights);
    }

    @Override
    public List<InstagramMediaInsight> findBySnapshotId(Long snapshotId) {
        return jpaRepository.findBySnapshotId(snapshotId);
    }
}