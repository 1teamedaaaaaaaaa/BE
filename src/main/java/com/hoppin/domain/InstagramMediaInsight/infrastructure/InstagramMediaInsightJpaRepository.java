package com.hoppin.domain.InstagramMediaInsight.infrastructure;

import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstagramMediaInsightJpaRepository
        extends JpaRepository<InstagramMediaInsight, Long> {

    List<InstagramMediaInsight> findBySnapshotId(Long snapshotId);

    List<InstagramMediaInsight> findBySnapshotIdAndTimestampGreaterThanEqual(
            Long snapshotId,
            String sinceDate
    );
}