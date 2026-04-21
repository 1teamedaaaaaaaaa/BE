package com.hoppin.domain.MusicPromotion.repository;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;

import java.util.List;
import java.util.Optional;

public interface MusicPromotionRepository {

    MusicPromotion save(MusicPromotion promotion);

    Optional<MusicPromotion> findById(Long promotionId);

    List<MusicPromotion> findByMusicianId(Long musicianId);

    boolean existsById(Long promotionId);
}
