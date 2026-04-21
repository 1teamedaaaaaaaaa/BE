package com.hoppin.domain.PromotionTrackingLink.infrastructure;

import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionTrackingLinkJpaRepository extends JpaRepository<PromotionTrackingLink, Long> {

    Optional<PromotionTrackingLink> findByTrackingCode(String trackingCode);

    boolean existsByTrackingCode(String trackingCode);

    List<PromotionTrackingLink> findByPromotionId(Long promotionId);
}
