package com.hoppin.domain.PromotionTrackingClick.repository;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;

public interface PromotionTrackingClickRepository {

    PromotionTrackingClick save(PromotionTrackingClick click);

    long countByPromotionId(Long promotionId);

    long countByTrackingLinkId(Long trackingLinkId);
}
