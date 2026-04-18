package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import lombok.RequiredArgsConstructor;
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
}
