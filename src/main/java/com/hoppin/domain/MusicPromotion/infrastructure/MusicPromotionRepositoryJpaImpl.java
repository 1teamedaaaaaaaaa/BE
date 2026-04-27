package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MusicPromotionRepositoryJpaImpl implements MusicPromotionRepository {

    private final MusicPromotionJpaRepository musicPromotionJpaRepository;

    @Override
    public MusicPromotion save(MusicPromotion promotion) {
        return musicPromotionJpaRepository.save(promotion);
    }

    @Override
    public Optional<MusicPromotion> findById(Long promotionId) {
        return musicPromotionJpaRepository.findById(promotionId);
    }

    @Override
    public List<MusicPromotion> findByMusicianId(Long musicianId) {
        return musicPromotionJpaRepository.findByMusicianId(musicianId);
    }

    @Override
    public boolean existsById(Long promotionId) {
        return musicPromotionJpaRepository.existsById(promotionId);
    }

    @Override
    public Page<MyPagePromotionItemResponse> findMyPagePromotions(
            Long musicianId,
            String keyword,
            Pageable pageable
    ) {
        return musicPromotionJpaRepository
                .findMyPagePromotions(musicianId, keyword, pageable)
                .map(this::toMyPagePromotionItemResponse);
    }

    @Override
    public void delete(MusicPromotion promotion) {
        musicPromotionJpaRepository.delete(promotion);
    }

    private MyPagePromotionItemResponse toMyPagePromotionItemResponse(MusicPromotion promotion) {
        return new MyPagePromotionItemResponse(
                promotion.getId(),
                promotion.getSongTitle(),
                promotion.getImageUrl(),
                promotion.getShareCount(),
                promotion.getProfileVisitCount(),
                promotion.getLinkClickCount()
        );
    }
}
