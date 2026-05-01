package com.hoppin.domain.MusicPromotion.service;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.dto.MusicPromotionDetailResponse;
import com.hoppin.domain.MusicPromotion.dto.UpdateMusicPromotionRequest;
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
import com.hoppin.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MusicPromotionService {

    private static final int MAX_TRACKING_CODE_GENERATION_ATTEMPTS = 10;

    private final MusicianRepository musicianRepository;
    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionTrackingLinkRepository trackingLinkRepository;
    private final PromotionTrackingClickRepository promotionTrackingClickRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;

    private final TrackingCodeGenerator trackingCodeGenerator;
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
                request.songTitle(),
                request.releaseDate(),
                request.imageUrl(),
                request.shortDescription()
        ));

        saveStreamingLinks(request.streamingLinks(), promotion);

        String trackingCode = generateUniqueTrackingCode();
        String backendBase = trimTrailingSlash(backendBaseUrl);
        String frontendBase = trimTrailingSlash(frontendBaseUrl);

        String trackingUrl = backendBase + "/r/" + trackingCode;
        String detailUrl = frontendBase + "/album/" + promotion.getId();

        trackingLinkRepository.save(new PromotionTrackingLink(
                promotion,
                PromotionChannel.INSTAGRAM,
                trackingCode,
                trackingUrl,
                detailUrl
        ));

        return CreateMusicPromotionResponse.from(trackingUrl, promotion.getId());
    }

    @Transactional(readOnly = true)
    public MusicPromotionDetailResponse getMusicPromotion(Long promotionId) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("음악 홍보를 찾을 수 없습니다."));

        List<PromotionStreamingLink> streamingLinks =
                promotionStreamingLinkRepository.findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId);

        return MusicPromotionDetailResponse.from(promotion, streamingLinks);
    }

    @Transactional
    public void updateMusicPromotion(Long musicianId, Long promotionId, UpdateMusicPromotionRequest request) {
        validateUpdateRequest(request);

        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("음악 홍보를 찾을 수 없습니다."));

        validateOwnership(musicianId, promotion);

        promotion.update(
                request.activityName(),
                request.songTitle(),
                request.releaseDate(),
                request.imageUrl(),
                request.shortDescription()
        );

        syncStreamingLinks(promotion, request.streamingLinks());
    }

    @Transactional
    public void deleteMusicPromotion(Long musicianId, Long promotionId) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("음악 홍보를 찾을 수 없습니다."));

        validateOwnership(musicianId, promotion);

        promotionTrackingClickRepository.deleteByPromotionId(promotionId);
        promotionStreamingClickRepository.deleteByPromotionId(promotionId);
        trackingLinkRepository.deleteByPromotionId(promotionId);
        promotionStreamingLinkRepository.deleteByPromotionId(promotionId);
        promotionDiagnosisRepository.deleteByMusicPromotion_Id(promotionId);
        musicPromotionRepository.delete(promotion);
    }

    private void syncStreamingLinks(
            MusicPromotion promotion,
            List<UpdateMusicPromotionRequest.UpdateStreamingLinkRequest> requestedLinks
    ) {
        List<PromotionStreamingLink> existingLinks =
                promotionStreamingLinkRepository.findByPromotionId(promotion.getId());

        Map<String, PromotionStreamingLink> existingLinkMap = new HashMap<>();
        for (PromotionStreamingLink existingLink : existingLinks) {
            existingLinkMap.put(existingLink.getStreamingCode(), existingLink);
        }

        Set<String> requestedStreamingCodes = new HashSet<>();
        String backendBase = trimTrailingSlash(backendBaseUrl);

        for (int i = 0; i < requestedLinks.size(); i++) {
            UpdateMusicPromotionRequest.UpdateStreamingLinkRequest requestedLink = requestedLinks.get(i);
            int displayOrder = i + 1;

            if (requestedLink.redirectUrl() == null || requestedLink.redirectUrl().isBlank()) {
                String originalUrl = requestedLink.url();
                String domain = streamingDomainExtractor.extract(originalUrl);
                String streamingCode = streamingCodeGenerator.generate();
                String redirectUrl = backendBase + "/s/" + streamingCode;

                PromotionStreamingLink newLink = new PromotionStreamingLink(
                        promotion,
                        streamingCode,
                        originalUrl,
                        domain,
                        redirectUrl,
                        displayOrder
                );

                promotionStreamingLinkRepository.save(newLink);
                continue;
            }

            String streamingCode = extractStreamingCode(requestedLink.redirectUrl(), backendBase);
            PromotionStreamingLink existingLink = existingLinkMap.get(streamingCode);
            if (existingLink == null) {
                throw new IllegalArgumentException("존재하지 않는 스트리밍 링크입니다. code=" + streamingCode);
            }

            String updatedUrl = requestedLink.url();
            String updatedDomain = streamingDomainExtractor.extract(updatedUrl);

            existingLink.update(updatedUrl, updatedDomain, displayOrder);
            requestedStreamingCodes.add(existingLink.getStreamingCode());
        }

        for (PromotionStreamingLink existingLink : existingLinks) {
            if (!requestedStreamingCodes.contains(existingLink.getStreamingCode())) {
                promotionStreamingClickRepository.deleteByStreamingLinkId(existingLink.getId());
                promotionStreamingLinkRepository.delete(existingLink);
            }
        }
    }

    private void saveStreamingLinks(
            List<CreateMusicPromotionRequest.CreateStreamingLinkRequest> streamingLinks,
            MusicPromotion promotion
    ) {
        String backendBase = trimTrailingSlash(backendBaseUrl);

        for (int i = 0; i < streamingLinks.size(); i++) {
            String originalUrl = streamingLinks.get(i).url();
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

    private String generateUniqueTrackingCode() {
        for (int attempt = 0; attempt < MAX_TRACKING_CODE_GENERATION_ATTEMPTS; attempt++) {
            String trackingCode = trackingCodeGenerator.generate();
            if (!trackingLinkRepository.existsByTrackingCode(trackingCode)) {
                return trackingCode;
            }
        }
        throw new IllegalStateException("추적 코드를 생성하지 못했습니다.");
    }

    private void validateOwnership(Long musicianId, MusicPromotion promotion) {
        if (!promotion.getMusician().getId().equals(musicianId)) {
            throw new IllegalArgumentException("해당 홍보를 수정 또는 삭제할 권한이 없습니다.");
        }
    }

    private void validateCreateRequest(CreateMusicPromotionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        requireText(request.activityName(), "활동명은 필수입니다.");
        requireText(request.songTitle(), "곡명은 필수입니다.");
        if (request.releaseDate() == null) {
            throw new IllegalArgumentException("발매일은 필수입니다.");
        }
        requireText(request.imageUrl(), "이미지 URL은 필수입니다.");
        requireText(request.shortDescription(), "짧은 설명은 필수입니다.");

        if (request.streamingLinks() == null || request.streamingLinks().isEmpty()) {
            throw new IllegalArgumentException("스트리밍 링크는 최소 1개 이상 등록해야 합니다.");
        }

        for (CreateMusicPromotionRequest.CreateStreamingLinkRequest link : request.streamingLinks()) {
            requireText(link.url(), "스트리밍 URL은 필수입니다.");
        }
    }

    private void validateUpdateRequest(UpdateMusicPromotionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 본문은 필수입니다.");
        }
        requireText(request.activityName(), "활동명은 필수입니다.");
        requireText(request.songTitle(), "곡명은 필수입니다.");
        if (request.releaseDate() == null) {
            throw new IllegalArgumentException("발매일은 필수입니다.");
        }
        requireText(request.imageUrl(), "이미지 URL은 필수입니다.");
        requireText(request.shortDescription(), "짧은 설명은 필수입니다.");

        if (request.streamingLinks() == null || request.streamingLinks().isEmpty()) {
            throw new IllegalArgumentException("스트리밍 링크는 최소 1개 이상 등록해야 합니다.");
        }

        for (UpdateMusicPromotionRequest.UpdateStreamingLinkRequest link : request.streamingLinks()) {
            requireText(link.url(), "스트리밍 URL은 필수입니다.");
        }
    }

    private String extractStreamingCode(String redirectUrl, String backendBase) {
        String expectedPrefix = backendBase + "/s/";
        if (redirectUrl == null || !redirectUrl.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException("유효하지 않은 스트리밍 redirectUrl 입니다.");
        }

        String streamingCode = redirectUrl.substring(expectedPrefix.length());
        if (streamingCode.isBlank() || streamingCode.contains("/")) {
            throw new IllegalArgumentException("유효하지 않은 스트리밍 redirectUrl 입니다.");
        }

        return streamingCode;
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
