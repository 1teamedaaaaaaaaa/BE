package com.hoppin.domain.Instagram.service;

import com.hoppin.domain.Instagram.client.InstagramGraphClient;
import com.hoppin.domain.Instagram.dto.InstagramInsightApiResponse;
import com.hoppin.domain.Instagram.dto.InstagramMediaListApiResponse;
import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramConnection.entity.InstagramConnection;
import com.hoppin.domain.InstagramConnection.repository.InstagramConnectionRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstagramMediaService {

    private final InstagramConnectionRepository instagramConnectionRepository;
    private final InstagramGraphClient instagramGraphClient;

    public List<InstagramMediaResponse> getMediaList(Long musicianId) {
        InstagramConnection connection = instagramConnectionRepository
                .findByMusicianId(musicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram 연동 정보를 찾을 수 없습니다."));

        InstagramMediaListApiResponse mediaList =
                instagramGraphClient.getMediaList(connection.getAccessToken());

        if (mediaList.data() == null || mediaList.data().isEmpty()) {
            return List.of();
        }

        return mediaList.data().stream()
                .map(media -> toResponse(media, connection.getAccessToken()))
                .toList();
    }

    private InstagramMediaResponse toResponse(
            InstagramMediaListApiResponse.InstagramMediaItem media,
            String accessToken
    ) {
        InstagramInsightApiResponse insights =
                instagramGraphClient.getMediaInsights(media.id(), accessToken);

        return new InstagramMediaResponse(
                media.id(),
                media.caption(),
                media.mediaType(),
                media.mediaUrl(),
                media.permalink(),
                media.thumbnailUrl(),
                media.timestamp(),
                insights.getMetricValue("shares"),
                insights.getMetricValue("profile_visits")
        );
    }
}
