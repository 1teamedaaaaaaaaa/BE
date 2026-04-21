package com.hoppin.domain.PromotionTrackingClick.infrastrcuture;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PromotionTrackingClickRepositoryJpaImpl implements PromotionTrackingClickRepository {

    private final PromotionTrackingClickJpaRepository trackingClickJpaRepository;

    @Override
    public PromotionTrackingClick save(PromotionTrackingClick click) {
        return trackingClickJpaRepository.save(click);
    }

    @Override
    public long countByPromotionId(Long promotionId) {
        return trackingClickJpaRepository.countByPromotionId(promotionId);
    }

    @Override
    public long countByTrackingLinkId(Long trackingLinkId) {
        return trackingClickJpaRepository.countByTrackingLinkId(trackingLinkId);
    }
}
