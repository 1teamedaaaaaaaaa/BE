package com.hoppin.domain.MusicPromotion.service;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingCodeGenerator;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingDomainExtractor;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionChannel;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
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
    private final PromotionTrackingClickRepository promotionTrackingClickRepository = mock(PromotionTrackingClickRepository.class);
    private final PromotionStreamingClickRepository promotionStreamingClickRepository = mock(PromotionStreamingClickRepository.class);
    private final PromotionDiagnosisRepository promotionDiagnosisRepository = mock(PromotionDiagnosisRepository.class);

    private final MusicPromotionService musicPromotionService =
            new MusicPromotionService(
                    musicianRepository,
                    musicPromotionRepository,
                    trackingLinkRepository,
                    promotionTrackingClickRepository,
                    promotionStreamingClickRepository,
                    promotionStreamingLinkRepository,
                    promotionDiagnosisRepository,
                    trackingCodeGenerator,
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

        when(trackingCodeGenerator.generate()).thenReturn("TRACK123");
        when(streamingCodeGenerator.generate()).thenReturn("STREAM1", "STREAM2");
        when(streamingDomainExtractor.extract("https://open.spotify.com/track/test")).thenReturn("spotify");
        when(streamingDomainExtractor.extract("https://music.youtube.com/watch?v=test")).thenReturn("youtube");

        ReflectionTestUtils.setField(musicPromotionService, "backendBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(musicPromotionService, "frontendBaseUrl", "http://localhost:5173");

        CreateMusicPromotionResponse response =
                musicPromotionService.createMusicPromotion(musicianId, request);

        assertThat(response).isNotNull();
        assertThat(response.trackingUrl()).isEqualTo("http://localhost:8080/r/TRACK123");

        ArgumentCaptor<MusicPromotion> promotionCaptor =
                ArgumentCaptor.forClass(MusicPromotion.class);

        verify(musicPromotionRepository).save(promotionCaptor.capture());

        MusicPromotion savedPromotion = promotionCaptor.getValue();

        assertThat(savedPromotion.getActivityName()).isEqualTo("첫 싱글 발매 프로모션");
        assertThat(savedPromotion.getInstagramAccount()).isEqualTo("@hoppin_artist");
        assertThat(savedPromotion.getSongTitle()).isEqualTo("Blue Night");
        assertThat(savedPromotion.getImageUrl()).contains("hoppin-s3-bucket");
        assertThat(savedPromotion.getShortDescription()).isEqualTo("첫 싱글 발매 홍보입니다.");

        verify(promotionStreamingLinkRepository, times(2)).save(any());
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
        verify(promotionStreamingLinkRepository, never()).save(any());
        verify(trackingLinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("홍보 조회 시 상세 정보와 스트리밍 링크를 반환한다")
    void getMusicPromotion_success() {
        Long promotionId = 10L;

        Musician musician = mock(Musician.class);
        MusicPromotion promotion = new MusicPromotion(
                musician,
                "첫 싱글 발매 프로모션",
                "@hoppin_artist",
                "Blue Night",
                LocalDate.of(2026, 4, 25),
                "https://hoppin-s3-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/test.jpg",
                "첫 싱글 발매 홍보입니다."
        );
        ReflectionTestUtils.setField(promotion, "id", promotionId);

        PromotionTrackingLink trackingLink = new PromotionTrackingLink(
                promotion,
                PromotionChannel.INSTAGRAM,
                "ABC123",
                "http://localhost:8080/r/ABC123",
                "http://localhost:5173/music-promotions/10"
        );

        PromotionStreamingLink streamingLink1 = new PromotionStreamingLink(
                promotion,
                "STREAM1",
                "https://open.spotify.com/track/test",
                "spotify",
                "http://localhost:8080/s/STREAM1",
                1
        );

        PromotionStreamingLink streamingLink2 = new PromotionStreamingLink(
                promotion,
                "STREAM2",
                "https://music.youtube.com/watch?v=test",
                "youtube",
                "http://localhost:8080/s/STREAM2",
                2
        );

        when(musicPromotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(trackingLinkRepository.findByPromotionId(promotionId)).thenReturn(List.of(trackingLink));
        when(promotionStreamingLinkRepository.findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId))
                .thenReturn(List.of(streamingLink1, streamingLink2));

        var response = musicPromotionService.getMusicPromotion(promotionId);

        assertThat(response).isNotNull();
        assertThat(response.promotionId()).isEqualTo(promotionId);
        assertThat(response.trackingCode()).isEqualTo("ABC123");
        assertThat(response.trackingUrl()).isEqualTo("http://localhost:8080/r/ABC123");
        assertThat(response.activityName()).isEqualTo("첫 싱글 발매 프로모션");
        assertThat(response.instagramAccount()).isEqualTo("@hoppin_artist");
        assertThat(response.songTitle()).isEqualTo("Blue Night");
        assertThat(response.streamingLinks()).hasSize(2);
        assertThat(response.streamingLinks().get(0).streamingCode()).isEqualTo("STREAM1");
        assertThat(response.streamingLinks().get(1).streamingCode()).isEqualTo("STREAM2");
    }

    @Test
    @DisplayName("존재하지 않는 홍보를 조회하면 실패한다")
    void getMusicPromotion_notFound_fail() {
        Long promotionId = 999L;

        when(musicPromotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> musicPromotionService.getMusicPromotion(promotionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("음악 홍보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("본인 홍보를 삭제하면 관련 하위 데이터도 모두 삭제한다")
    void deleteMusicPromotion_success() {
        Long musicianId = 1L;
        Long promotionId = 10L;

        Musician musician = new Musician("tester", "tester@example.com");
        ReflectionTestUtils.setField(musician, "id", musicianId);

        MusicPromotion promotion = new MusicPromotion(
                musician,
                "첫 싱글 발매 프로모션",
                "@hoppin_artist",
                "Blue Night",
                LocalDate.of(2026, 4, 25),
                "https://hoppin-s3-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/test.jpg",
                "첫 싱글 발매 홍보입니다."
        );
        ReflectionTestUtils.setField(promotion, "id", promotionId);

        when(musicPromotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        musicPromotionService.deleteMusicPromotion(musicianId, promotionId);

        verify(promotionTrackingClickRepository).deleteByPromotionId(promotionId);
        verify(promotionStreamingClickRepository).deleteByPromotionId(promotionId);
        verify(trackingLinkRepository).deleteByPromotionId(promotionId);
        verify(promotionStreamingLinkRepository).deleteByPromotionId(promotionId);
        verify(promotionDiagnosisRepository).deleteByMusicPromotion_Id(promotionId);
        verify(musicPromotionRepository).delete(promotion);
    }

    @Test
    @DisplayName("존재하지 않는 홍보를 삭제하면 실패한다")
    void deleteMusicPromotion_notFound_fail() {
        Long musicianId = 1L;
        Long promotionId = 999L;

        when(musicPromotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> musicPromotionService.deleteMusicPromotion(musicianId, promotionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("음악 홍보를 찾을 수 없습니다.");

        verify(promotionTrackingClickRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionStreamingClickRepository, never()).deleteByPromotionId(anyLong());
        verify(trackingLinkRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionStreamingLinkRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionDiagnosisRepository, never()).deleteByMusicPromotion_Id(anyLong());
        verify(musicPromotionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("다른 뮤지션의 홍보를 삭제하려고 하면 실패한다")
    void deleteMusicPromotion_forbidden_fail() {
        Long requestMusicianId = 1L;
        Long ownerMusicianId = 2L;
        Long promotionId = 10L;

        Musician musician = new Musician("owner", "owner@example.com");
        ReflectionTestUtils.setField(musician, "id", ownerMusicianId);

        MusicPromotion promotion = new MusicPromotion(
                musician,
                "첫 싱글 발매 프로모션",
                "@hoppin_artist",
                "Blue Night",
                LocalDate.of(2026, 4, 25),
                "https://hoppin-s3-bucket.s3.ap-northeast-2.amazonaws.com/music-promotions/test.jpg",
                "첫 싱글 발매 홍보입니다."
        );
        ReflectionTestUtils.setField(promotion, "id", promotionId);

        when(musicPromotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        assertThatThrownBy(() -> musicPromotionService.deleteMusicPromotion(requestMusicianId, promotionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 홍보를 삭제할 권한이 없습니다.");

        verify(promotionTrackingClickRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionStreamingClickRepository, never()).deleteByPromotionId(anyLong());
        verify(trackingLinkRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionStreamingLinkRepository, never()).deleteByPromotionId(anyLong());
        verify(promotionDiagnosisRepository, never()).deleteByMusicPromotion_Id(anyLong());
        verify(musicPromotionRepository, never()).delete(any());
    }
}