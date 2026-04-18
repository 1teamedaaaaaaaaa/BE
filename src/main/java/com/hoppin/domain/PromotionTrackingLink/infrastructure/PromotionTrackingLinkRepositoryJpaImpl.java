package com.hoppin.domain.PromotionTrackingLink.infrastructure;

import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PromotionTrackingLinkRepositoryJpaImpl implements PromotionTrackingLinkRepository {

    private final PromotionTrackingLinkJpaRepository trackingLinkJpaRepository;

    @Override
    public PromotionTrackingLink save(PromotionTrackingLink trackingLink) {
        return trackingLinkJpaRepository.save(trackingLink);
    }

    @Override
    public Optional<PromotionTrackingLink> findById(Long trackingLinkId) {
        return trackingLinkJpaRepository.findById(trackingLinkId);
    }

    @Override
    public Optional<PromotionTrackingLink> findByTrackingCode(String trackingCode) {
        return trackingLinkJpaRepository.findByTrackingCode(trackingCode);
    }

    @Override
    public boolean existsByTrackingCode(String trackingCode) {
        return trackingLinkJpaRepository.existsByTrackingCode(trackingCode);
    }

    @Override
    public List<PromotionTrackingLink> findByPromotionId(Long promotionId) {
        return trackingLinkJpaRepository.findByPromotionId(promotionId);
    }
}
