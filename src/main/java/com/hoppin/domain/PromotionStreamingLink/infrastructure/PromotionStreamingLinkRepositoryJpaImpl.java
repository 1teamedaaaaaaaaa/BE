package com.hoppin.domain.PromotionStreamingLink.infrastructure;

import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PromotionStreamingLinkRepositoryJpaImpl implements PromotionStreamingLinkRepository {

    private final PromotionStreamingLinkJpaRepository promotionStreamingLinkJpaRepository;

    @Override
    public PromotionStreamingLink save(PromotionStreamingLink streamingLink) {
        return promotionStreamingLinkJpaRepository.save(streamingLink);
    }

    @Override
    public List<PromotionStreamingLink> saveAll(List<PromotionStreamingLink> streamingLinks) {
        return promotionStreamingLinkJpaRepository.saveAll(streamingLinks);
    }

    @Override
    public Optional<PromotionStreamingLink> findByStreamingCode(String streamingCode) {
        return promotionStreamingLinkJpaRepository.findByStreamingCode(streamingCode);
    }

    @Override
    public List<PromotionStreamingLink> findByPromotionId(Long promotionId) {
        return promotionStreamingLinkJpaRepository.findByPromotionId(promotionId);
    }

    @Override
    public List<PromotionStreamingLink> findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(Long promotionId) {
        return promotionStreamingLinkJpaRepository
                .findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId);
    }

    @Override
    public void deleteByPromotionId(Long promotionId) {
        promotionStreamingLinkJpaRepository.deleteByPromotionId(promotionId);
    }

    @Override
    public void delete(PromotionStreamingLink streamingLink) {
        promotionStreamingLinkJpaRepository.delete(streamingLink);
    }
}
