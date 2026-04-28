package com.hoppin.domain.PromotionStreamingClick.repository;

import com.hoppin.domain.PromotionStreamingClick.entity.PromotionStreamingClick;

public interface PromotionStreamingClickRepository {

    PromotionStreamingClick save(PromotionStreamingClick streamingClick);

    void deleteByPromotionId(Long promotionId);

    void deleteByStreamingLinkId(Long streamingLinkId);
}
