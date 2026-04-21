package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicPromotionJpaRepository extends JpaRepository<MusicPromotion, Long> {

    List<MusicPromotion> findByMusicianId(Long musicianId);
}
