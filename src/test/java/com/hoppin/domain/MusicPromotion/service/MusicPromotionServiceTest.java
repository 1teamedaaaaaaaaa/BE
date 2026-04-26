package com.hoppin.domain.MusicPromotion.service;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingCodeGenerator;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingDomainExtractor;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPromotionServiceTest {

    private final MusicianRepository musicianRepository = mock(MusicianRepository.class);
    private final MusicPromotionRepository musicPromotionRepository = mock(MusicPromotionRepository.class);
    private final PromotionTrackingLinkRepository trackingLinkRepository = mock(PromotionTrackingLinkRepository.class);
    private final TrackingCodeGenerator trackingCodeGenerator = mock(TrackingCodeGenerator.class);
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository = mock(PromotionStreamingLinkRepository.class);
    private final StreamingCodeGenerator streamingCodeGenerator = mock(StreamingCodeGenerator.class);
    private final StreamingDomainExtractor streamingDomainExtractor = mock(StreamingDomainExtractor.class);

    private final MusicPromotionService musicPromotionService =
            new MusicPromotionService(
                    musicianRepository,
                    musicPromotionRepository,
                    trackingLinkRepository,
                    trackingCodeGenerator,
                    promotionStreamingLinkRepository,
                    streamingCodeGenerator,
                    streamingDomainExtractor
            );
    @Test
    @DisplayName("홍보 생성 시 S3 이미지 URL과 스트리밍 링크를 저장하고 트래킹 URL을 반환한다")
    void createMusicPromotion_success() {
        Long musicianId = 1L;

        Musician musician = mock(Musician.class);

        CreateMusicPromotionRequest request = new CreateMusicPromotionRequest(
                "첫 싱글 발매 프로모션",
                "@hoppin_artist",
                "Blue Night",
                LocalDate.of(2026, 4, 25),
                List.of(
                        new CreateMusicPromotionRequest.StreamingLinkRequest("https://open.spotify.com/track/test"),
                        new CreateMusicPromotionRequest.StreamingLinkRequest("https://music.youtube.com/watch?v=test")
                ),
                "https://hoppin-s3-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/test.jpg",
                "첫 싱글 발매 홍보입니다."
        );

        when(musicianRepository.findById(musicianId)).thenReturn(Optional.of(musician));

        when(musicPromotionRepository.save(any(MusicPromotion.class)))
                .thenAnswer(invocation -> {
                    MusicPromotion promotion = invocation.getArgument(0);
                    ReflectionTestUtils.setField(promotion, "id", 10L);
                    return promotion;
                });

        when(trackingLinkRepository.existsByTrackingCode(anyString()))
                .thenReturn(false);

        ReflectionTestUtils.setField(musicPromotionService, "backendBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(musicPromotionService, "frontendBaseUrl", "http://localhost:5173");

        CreateMusicPromotionResponse response =
                musicPromotionService.createMusicPromotion(musicianId, request);

        assertThat(response).isNotNull();
        assertThat(response.trackingUrl()).startsWith("http://localhost:8080/r/");

        ArgumentCaptor<MusicPromotion> promotionCaptor =
                ArgumentCaptor.forClass(MusicPromotion.class);

        verify(musicPromotionRepository).save(promotionCaptor.capture());

        MusicPromotion savedPromotion = promotionCaptor.getValue();

        assertThat(savedPromotion.getActivityName()).isEqualTo("첫 싱글 발매 프로모션");
        assertThat(savedPromotion.getInstagramAccount()).isEqualTo("@hoppin_artist");
        assertThat(savedPromotion.getSongTitle()).isEqualTo("Blue Night");
        assertThat(savedPromotion.getImageUrl()).contains("hoppin-s3-bucket");
        assertThat(savedPromotion.getShortDescription()).isEqualTo("첫 싱글 발매 홍보입니다.");

        //verify(streamingLinkRepository, times(2)).save(any());
        verify(trackingLinkRepository).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 뮤지션이면 홍보 생성에 실패한다")
    void createMusicPromotion_musicianNotFound_fail() {
        Long musicianId = 999L;

        CreateMusicPromotionRequest request = new CreateMusicPromotionRequest(
                "첫 싱글 발매 프로모션",
                "@hoppin_artist",
                "Blue Night",
                LocalDate.of(2026, 4, 25),
                List.of(
                        new CreateMusicPromotionRequest.StreamingLinkRequest("https://musicpeak.site")
                ),
                "https://hoppin-s3-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/test.jpg",
                "첫 싱글 발매 홍보입니다."
        );

        when(musicianRepository.findById(musicianId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                musicPromotionService.createMusicPromotion(musicianId, request)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("뮤지션을 찾을 수 없습니다.");

        verify(musicPromotionRepository, never()).save(any());
        //verify(streamingLinkRepository, never()).save(any());
        verify(trackingLinkRepository, never()).save(any());
    }
}