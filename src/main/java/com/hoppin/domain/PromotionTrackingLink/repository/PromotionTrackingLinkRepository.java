package com.hoppin.domain.PromotionTrackingLink.repository;

import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;

import java.util.List;
import java.util.Optional;

public interface PromotionTrackingLinkRepository {

    PromotionTrackingLink save(PromotionTrackingLink trackingLink);

    Optional<PromotionTrackingLink> findById(Long trackingLinkId);

    Optional<PromotionTrackingLink> findByTrackingCode(String trackingCode);

    boolean existsByTrackingCode(String trackingCode);

    Optional<PromotionTrackingLink> findFirstByPromotionId(Long promotionId);

    List<PromotionTrackingLink> findByPromotionId(Long promotionId);

    void deleteByPromotionId(Long promotionId);
}
