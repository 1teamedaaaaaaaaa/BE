package com.hoppin.domain.MusicPromotion.service;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.dto.MusicPromotionDetailResponse;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingCodeGenerator;
import com.hoppin.domain.PromotionStreamingLink.helper.StreamingDomainExtractor;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionChannel;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicPromotionService {

    private static final int MAX_TRACKING_CODE_GENERATION_ATTEMPTS = 10;

    private final MusicianRepository musicianRepository;
    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionTrackingLinkRepository trackingLinkRepository;
    private final TrackingCodeGenerator trackingCodeGenerator;
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final StreamingCodeGenerator streamingCodeGenerator;
    private final StreamingDomainExtractor streamingDomainExtractor;

    @Value("${app.backend-base-url}")
    private String backendBaseUrl;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Transactional
    public CreateMusicPromotionResponse createMusicPromotion(Long musicianId, CreateMusicPromotionRequest request) {
        validateCreateRequest(request);

        Musician musician = musicianRepository.findById(musicianId)
                .orElseThrow(() -> new ResourceNotFoundException("뮤지션을 찾을 수 없습니다."));

        MusicPromotion promotion = musicPromotionRepository.save(new MusicPromotion(
                musician,
                request.activityName(),
                request.instagramAccount(),
                request.songTitle(),
                request.releaseDate(),
                request.imageUrl(),
                request.shortDescription()
        ));

        saveStreamingLinks(request, promotion);
        String trackingCode = generateUniqueTrackingCode();

        String backendBase = trimTrailingSlash(backendBaseUrl);
        String frontendBase = trimTrailingSlash(frontendBaseUrl);

        String trackingUrl = backendBase + "/r/" + trackingCode;
        String detailUrl = frontendBase + "/music-promotions/" + promotion.getId();

        trackingLinkRepository.save(new PromotionTrackingLink(
                promotion,
                PromotionChannel.INSTAGRAM,
                trackingCode,
                trackingUrl,
                detailUrl
        ));

        return CreateMusicPromotionResponse.from(trackingUrl);
    }

    @Transactional(readOnly = true)
    public MusicPromotionDetailResponse getMusicPromotion(Long promotionId) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("음악 홍보를 찾을 수 없습니다."));

        PromotionTrackingLink trackingLink = trackingLinkRepository.findByPromotionId(promotionId)
                .stream()
                .filter(link -> link.getChannel() == PromotionChannel.INSTAGRAM)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("홍보 추적 링크를 찾을 수 없습니다."));

        List<PromotionStreamingLink> streamingLinks =
                promotionStreamingLinkRepository.findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId);

        return MusicPromotionDetailResponse.from(promotion, trackingLink, streamingLinks);
    }


    // TODO: 이 아래에 있는 함수들은 Helper로 따로 정리하기
    private String generateUniqueTrackingCode() {
        for (int attempt = 0; attempt < MAX_TRACKING_CODE_GENERATION_ATTEMPTS; attempt++) {
            String trackingCode = trackingCodeGenerator.generate();
            if (!trackingLinkRepository.existsByTrackingCode(trackingCode)) {
                return trackingCode;
            }
        }
        throw new IllegalStateException("추적 코드를 생성하지 못했습니다.");
    }

    private void saveStreamingLinks(CreateMusicPromotionRequest request, MusicPromotion promotion) {
        String backendBase = trimTrailingSlash(backendBaseUrl);

        for (int i = 0; i < request.streamingLinks().size(); i++) {
            String originalUrl = request.streamingLinks().get(i).url();
            String domain = streamingDomainExtractor.extract(originalUrl);
            String streamingCode = streamingCodeGenerator.generate();
            String redirectUrl = backendBase + "/s/" + streamingCode;
            int displayOrder = i + 1;

            PromotionStreamingLink streamingLink = new PromotionStreamingLink(
                    promotion,
                    streamingCode,
                    originalUrl,
                    domain,
                    redirectUrl,
                    displayOrder
            );

            promotionStreamingLinkRepository.save(streamingLink);
        }
    }

    private void validateCreateRequest(CreateMusicPromotionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        requireText(request.activityName(), "활동명은 필수입니다.");
        requireText(request.instagramAccount(), "인스타그램 계정은 필수입니다.");
        requireText(request.songTitle(), "곡명은 필수입니다.");
        if (request.releaseDate() == null) {
            throw new IllegalArgumentException("발매일은 필수입니다.");
        }
        requireText(request.imageUrl(), "이미지 URL은 필수입니다.");
        requireText(request.shortDescription(), "짧은 설명은 필수입니다.");

        if (request.streamingLinks() == null || request.streamingLinks().isEmpty()) {
            throw new IllegalArgumentException("스트리밍 링크는 최소 1개 이상 등록해야 합니다.");
        }

        for (CreateMusicPromotionRequest.StreamingLinkRequest link : request.streamingLinks()) {
            requireText(link.url(), "스트리밍 URL은 필수입니다.");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
    private String trimTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}

