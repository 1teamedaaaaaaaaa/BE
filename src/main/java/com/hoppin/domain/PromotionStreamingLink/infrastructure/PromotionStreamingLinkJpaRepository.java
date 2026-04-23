package com.hoppin.domain.PromotionStreamingLink.infrastructure;

import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionStreamingLinkJpaRepository extends JpaRepository<PromotionStreamingLink, Long> {

    Optional<PromotionStreamingLink> findByStreamingCode(String streamingCode);

    List<PromotionStreamingLink> findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(Long promotionId);
}
