package com.hoppin.domain.PromotionTrackingClick.repository;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionTrackingClickRepository {

    PromotionTrackingClick save(PromotionTrackingClick click);

    long countByPromotionId(Long promotionId);

    long countByTrackingLinkId(Long trackingLinkId);

    void deleteByPromotionId(Long promotionId);

    long countByPromotionIdAndClickedAtAfter(Long promotionId, LocalDateTime sinceDateTime);

    List<PromotionTrackingClick> findByPromotionIdAndClickedAtAfter(
            Long promotionId,
            LocalDateTime sinceDateTime
    );
}
