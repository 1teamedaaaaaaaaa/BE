package com.hoppin;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.Musician.entity.Musician;
import com.hoppin.domain.Musician.repository.MusicianRepository;
import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionChannel;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class RepositoryDBTest {

    @Autowired
    MusicianRepository musicianRepository;

    @Autowired
    MusicPromotionRepository musicPromotionRepository;

    @Autowired
    PromotionTrackingLinkRepository trackingLinkRepository;

    @Autowired
    PromotionTrackingClickRepository trackingClickRepository;

    @Autowired
    org.springframework.core.env.Environment env;

    @Test
    void dbConnectionAndRepositorySmokeTest() {
        Musician musician = musicianRepository.save(
                new Musician("뮤지션 A", "musician-a-test@example.com")
        );

        MusicPromotion promotion = musicPromotionRepository.save(
                new MusicPromotion(
                        musician,
                        "신곡 발매 홍보",
                        "musician_a",
                        "밤의 파도",
                        LocalDate.of(2026, 5, 1),
                        "https://open.spotify.com/track/example",
                        "https://cdn.example.com/image.jpg",
                        "새벽 감성을 담은 신곡"
                )
        );

        PromotionTrackingLink trackingLink = trackingLinkRepository.save(
                new PromotionTrackingLink(
                        promotion,
                        PromotionChannel.INSTAGRAM,
                        "a7x9k2",
                        "http://localhost:8080/r/a7x9k2",
                        "http://localhost:5173/music-promotions/" + promotion.getId()
                )
        );

        PromotionTrackingClick click = trackingClickRepository.save(
                new PromotionTrackingClick(
                        trackingLink,
                        "http://localhost:8080/r/a7x9k2",
                        "127.0.0.1",
                        "test-agent",
                        "https://l.instagram.com/"
                )
        );

        assertThat(musician.getId()).isNotNull();
        assertThat(promotion.getId()).isNotNull();
        assertThat(trackingLink.getId()).isNotNull();
        assertThat(click.getId()).isNotNull();

        assertThat(trackingLinkRepository.findByTrackingCode("a7x9k2")).isPresent();
        assertThat(trackingClickRepository.countByPromotionId(promotion.getId())).isEqualTo(1);
    }
}
