package com.hoppin.domain.InstagramMediaInsight.service;

import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramConnection.service.InstagramMediaService;
import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;
import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;
import com.hoppin.domain.InstagramMediaInsight.repository.InstagramMediaInsightRepository;
import com.hoppin.domain.InstagramMediaInsight.repository.MusicianInstagramInsightSnapshotRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstagramMediaInsightService {

    private final InstagramMediaService instagramMediaService;
    private final MusicianRepository musicianRepository;
    private final MusicianInstagramInsightSnapshotRepository snapshotRepository;
    private final InstagramMediaInsightRepository mediaInsightRepository;

    @Transactional
    public List<InstagramMediaResponse> syncMediaInsights(Long musicianId) {
        Musician musician = musicianRepository.findById(musicianId)
                .orElseThrow(() -> new ResourceNotFoundException("뮤지션을 찾을 수 없습니다."));

        List<InstagramMediaResponse> mediaResponses =
                instagramMediaService.getMediaList(musicianId);

        long totalShareCount = mediaResponses.stream()
                .mapToLong(response -> safeLong(response.shareCount()))
                .sum();

        long totalProfileVisitCount = mediaResponses.stream()
                .mapToLong(response -> safeLong(response.profileVisitCount()))
                .sum();

        long totalReachCount = mediaResponses.stream()
                .mapToLong(response -> safeLong(response.reachCount()))
                .sum();

        MusicianInstagramInsightSnapshot snapshot =
                snapshotRepository.save(new MusicianInstagramInsightSnapshot(
                        musician,
                        totalShareCount,
                        totalProfileVisitCount,
                        totalReachCount,
                        mediaResponses.size()
                ));

        List<InstagramMediaInsight> insights = mediaResponses.stream()
                .map(response -> new InstagramMediaInsight(
                        snapshot,
                        response.mediaId(),
                        response.caption(),
                        response.mediaType(),
                        response.mediaUrl(),
                        response.permalink(),
                        response.thumbnailUrl(),
                        response.timestamp(),
                        safeLong(response.shareCount()),
                        safeLong(response.profileVisitCount()),
                        safeLong(response.reachCount())
                ))
                .toList();

        mediaInsightRepository.saveAll(insights);

        return mediaResponses;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private long safeLong(Integer value) {
        return value == null ? 0L : value.longValue();
    }
}