package com.hoppin.domain.PromotionStreamingClick.infrastructure;

import com.hoppin.domain.PromotionStreamingClick.entity.PromotionStreamingClick;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PromotionStreamingClickRepositoryJpaImpl implements PromotionStreamingClickRepository {

    private final PromotionStreamingClickJpaRepository promotionStreamingClickJpaRepository;

    @Override
    public PromotionStreamingClick save(PromotionStreamingClick streamingClick) {
        return promotionStreamingClickJpaRepository.save(streamingClick);
    }

    @Override
    public void deleteByPromotionId(Long promotionId) {
        promotionStreamingClickJpaRepository.deleteByPromotionId(promotionId);
    }
}
