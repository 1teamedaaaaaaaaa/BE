package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MusicPromotionJpaRepository extends JpaRepository<MusicPromotion, Long> {

    List<MusicPromotion> findByMusicianId(Long musicianId);

    @Query("""
    select p
    from MusicPromotion p
    where p.musician.id = :musicianId
      and (:keyword is null or :keyword = '' or lower(p.songTitle) like lower(concat(:keyword, '%')))
    order by (
        select count(c)
        from PromotionTrackingClick c
        where c.promotion = p
        ) desc
    """)
    Page<MusicPromotion> findMyPagePromotions(
            @Param("musicianId") Long musicianId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
