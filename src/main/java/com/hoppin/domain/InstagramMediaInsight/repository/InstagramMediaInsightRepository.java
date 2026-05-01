package com.hoppin.domain.InstagramMediaInsight.repository;

import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;

import java.util.List;

public interface InstagramMediaInsightRepository {

    InstagramMediaInsight save(InstagramMediaInsight insight);

    List<InstagramMediaInsight> saveAll(List<InstagramMediaInsight> insights);

    List<InstagramMediaInsight> findBySnapshotId(Long snapshotId);
}