package com.hoppin.domain.PromotionStreamingLink.repository;

import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;

import java.util.List;
import java.util.Optional;

public interface PromotionStreamingLinkRepository {

    PromotionStreamingLink save(PromotionStreamingLink streamingLink);

    List<PromotionStreamingLink> saveAll(List<PromotionStreamingLink> streamingLinks);

    Optional<PromotionStreamingLink> findByStreamingCode(String streamingCode);

    List<PromotionStreamingLink> findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(Long promotionId);
}
