package com.hoppin.domain.MusicPromotion.service;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.dto.MusicPromotionDetailResponse;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.Musician.entity.Musician;
import com.hoppin.domain.Musician.repository.MusicianRepository;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionChannel;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicPromotionService {

    private static final int MAX_TRACKING_CODE_GENERATION_ATTEMPTS = 10;

    private final MusicianRepository musicianRepository;
    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionTrackingLinkRepository trackingLinkRepository;
    private final TrackingCodeGenerator trackingCodeGenerator;

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
                request.streamingUrl(),
                request.imageUrl(),
                request.shortDescription()
        ));

        String trackingCode = generateUniqueTrackingCode();
        String trackingUrl = backendBaseUrl + "/r/" + trackingCode;
        String detailUrl = frontendBaseUrl + "/music-promotions/" + promotion.getId();

        PromotionTrackingLink trackingLink = trackingLinkRepository.save(new PromotionTrackingLink(
                promotion,
                PromotionChannel.INSTAGRAM,
                trackingCode,
                trackingUrl,
                detailUrl
        ));

        return CreateMusicPromotionResponse.from(promotion, trackingLink, detailUrl);
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

        String detailUrl = frontendBaseUrl + "/music-promotions/" + promotion.getId();

        return MusicPromotionDetailResponse.from(promotion, trackingLink);
    }


    // Helper
    private String generateUniqueTrackingCode() {
        for (int attempt = 0; attempt < MAX_TRACKING_CODE_GENERATION_ATTEMPTS; attempt++) {
            String trackingCode = trackingCodeGenerator.generate();
            if (!trackingLinkRepository.existsByTrackingCode(trackingCode)) {
                return trackingCode;
            }
        }
        throw new IllegalStateException("추적 코드를 생성하지 못했습니다.");
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
        requireText(request.streamingUrl(), "스트리밍 URL은 필수입니다.");
        requireText(request.imageUrl(), "이미지 URL은 필수입니다.");
        requireText(request.shortDescription(), "짧은 설명은 필수입니다.");
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

