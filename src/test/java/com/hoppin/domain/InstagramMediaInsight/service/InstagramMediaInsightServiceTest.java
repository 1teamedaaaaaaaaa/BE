package com.hoppin.domain.InstagramMediaInsight.service;

import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramConnection.service.InstagramMediaService;
import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;
import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;
import com.hoppin.domain.InstagramMediaInsight.repository.InstagramMediaInsightRepository;
import com.hoppin.domain.InstagramMediaInsight.repository.MusicianInstagramInsightSnapshotRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstagramMediaInsightServiceTest {

    @Mock
    private InstagramMediaService instagramMediaService;

    @Mock
    private MusicianRepository musicianRepository;

    @Mock
    private MusicianInstagramInsightSnapshotRepository snapshotRepository;

    @Mock
    private InstagramMediaInsightRepository mediaInsightRepository;

    @InjectMocks
    private InstagramMediaInsightService instagramMediaInsightService;

    @Test
    void 인스타_게시물_지표를_뮤지션에_연결해서_스냅샷과_상세지표로_저장한다() throws Exception {
        // given
        Long musicianId = 7L;

        Musician musician = mock(Musician.class);

        List<InstagramMediaResponse> mockResponses = List.of(
                new InstagramMediaResponse(
                        "media-1",
                        "첫 번째 게시물",
                        "IMAGE",
                        "https://image-1.jpg",
                        "https://instagram.com/p/1",
                        "https://thumb-1.jpg",
                        "2026-05-01T10:00:00+0000",
                        10L,
                        5L,
                        100L
                ),
                new InstagramMediaResponse(
                        "media-2",
                        "두 번째 게시물",
                        "VIDEO",
                        "https://image-2.jpg",
                        "https://instagram.com/p/2",
                        "https://thumb-2.jpg",
                        "2026-05-01T11:00:00+0000",
                        20L,
                        7L,
                        200L
                )
        );

        when(musicianRepository.findById(musicianId))
                .thenReturn(Optional.of(musician));

        when(instagramMediaService.getMediaList(musicianId))
                .thenReturn(mockResponses);

        when(snapshotRepository.save(any(MusicianInstagramInsightSnapshot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<InstagramMediaResponse> result =
                instagramMediaInsightService.syncMediaInsights(musicianId);

        // then
        assertThat(result).hasSize(2);

        ArgumentCaptor<MusicianInstagramInsightSnapshot> snapshotCaptor =
                ArgumentCaptor.forClass(MusicianInstagramInsightSnapshot.class);

        verify(snapshotRepository).save(snapshotCaptor.capture());

        MusicianInstagramInsightSnapshot savedSnapshot = snapshotCaptor.getValue();

        assertThat(getField(savedSnapshot, "musician")).isEqualTo(musician);
        assertThat(getField(savedSnapshot, "totalShareCount")).isEqualTo(30L);
        assertThat(getField(savedSnapshot, "totalProfileVisitCount")).isEqualTo(12L);
        assertThat(getField(savedSnapshot, "totalReachCount")).isEqualTo(300L);
        assertThat(getField(savedSnapshot, "mediaCount")).isEqualTo(2);

        ArgumentCaptor<List<InstagramMediaInsight>> insightsCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(mediaInsightRepository).saveAll(insightsCaptor.capture());

        List<InstagramMediaInsight> savedInsights = insightsCaptor.getValue();

        assertThat(savedInsights).hasSize(2);
        assertThat(getField(savedInsights.get(0), "snapshot")).isEqualTo(savedSnapshot);
        assertThat(getField(savedInsights.get(0), "mediaId")).isEqualTo("media-1");
        assertThat(getField(savedInsights.get(0), "shareCount")).isEqualTo(10L);
        assertThat(getField(savedInsights.get(1), "snapshot")).isEqualTo(savedSnapshot);
        assertThat(getField(savedInsights.get(1), "mediaId")).isEqualTo("media-2");
        assertThat(getField(savedInsights.get(1), "reachCount")).isEqualTo(200L);
    }

    private Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}