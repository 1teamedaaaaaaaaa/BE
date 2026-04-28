package com.hoppin.domain.MusicPromotion.repository;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MusicPromotionRepository {

    MusicPromotion save(MusicPromotion promotion);

    Optional<MusicPromotion> findById(Long promotionId);

    void delete(MusicPromotion promotion);

    List<MusicPromotion> findByMusicianId(Long musicianId);

    boolean existsById(Long promotionId);

    Page<MyPagePromotionItemResponse> findMyPagePromotions(
            Long musicianId,
            String keyword,
            Pageable pageable
    );
}
