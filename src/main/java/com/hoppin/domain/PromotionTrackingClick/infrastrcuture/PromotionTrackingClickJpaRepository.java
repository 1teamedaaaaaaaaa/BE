package com.hoppin.domain.PromotionTrackingClick.infrastrcuture;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionTrackingClickJpaRepository extends JpaRepository<PromotionTrackingClick, Long> {

    long countByPromotionId(Long promotionId);

    long countByTrackingLinkId(Long trackingLinkId);
}
