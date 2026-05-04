package com.hoppin.domain.PromotionTrackingClick.infrastrcuture;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionTrackingClickJpaRepository extends JpaRepository<PromotionTrackingClick, Long> {

    long countByPromotionId(Long promotionId);

    long countByTrackingLinkId(Long trackingLinkId);

    void deleteByPromotionId(Long promotionId);

    long countByPromotionIdAndClickedAtGreaterThanEqual(
            Long promotionId,
            LocalDateTime sinceDateTime
    );

    List<PromotionTrackingClick> findByPromotionIdAndClickedAtGreaterThanEqual(
            Long promotionId,
            LocalDateTime sinceDateTime
    );
}
