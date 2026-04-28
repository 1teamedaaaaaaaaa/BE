package com.hoppin.domain.PromotionStreamingClick.infrastructure;

import com.hoppin.domain.PromotionStreamingClick.entity.PromotionStreamingClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionStreamingClickJpaRepository extends JpaRepository<PromotionStreamingClick, Long> {

    void deleteByPromotionId(Long promotionId);

    void deleteByStreamingLinkId(Long streamingLinkId);
}
