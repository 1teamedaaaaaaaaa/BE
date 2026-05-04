package com.hoppin.domain.PromotionTrackingClick.infrastrcuture;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public void deleteByPromotionId(Long promotionId) {
        trackingClickJpaRepository.deleteByPromotionId(promotionId);
    }

    @Override
    public long countByPromotionIdAndClickedAtAfter(Long promotionId, LocalDateTime sinceDateTime) {
        return trackingClickJpaRepository.countByPromotionIdAndClickedAtGreaterThanEqual(promotionId, sinceDateTime);
    }

    @Override
    public List<PromotionTrackingClick> findByPromotionIdAndClickedAtAfter(
            Long promotionId,
            LocalDateTime sinceDateTime
    ) {
        return trackingClickJpaRepository.findByPromotionIdAndClickedAtGreaterThanEqual(promotionId, sinceDateTime);
    }
}
