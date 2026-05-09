package com.hoppin.domain.analysis.service;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.analysis.dto.PromotionAnalysisPageResponse;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.infra.crawling.entity.PromotionAnalysisJob;
import com.hoppin.infra.crawling.enumtype.AnalysisJobStatus;
import com.hoppin.infra.crawling.repository.PromotionAnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionAnalysisPageQueryService {

    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionTrackingClickRepository promotionTrackingClickRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final PromotionAnalysisJobRepository promotionAnalysisJobRepository;

    public PromotionAnalysisPageResponse getAnalysisPage(
            Long musicianId,
            Long promotionId
    ) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(promotion, musicianId);

        long trackingClickCount =
                promotionTrackingClickRepository.countByPromotionId(promotionId);

        long streamingClickCount =
                promotionStreamingClickRepository.countByPromotionId(promotionId);

        return PromotionAnalysisPageResponse.builder()
                .promotionId(promotion.getId())
                .activityName(promotion.getActivityName())
                .songTitle(promotion.getSongTitle())
                .releaseDate(promotion.getReleaseDate())
                .imageUrl(promotion.getImageUrl())
                .shortDescription(promotion.getShortDescription())
                .createdAt(promotion.getCreatedAt())
                .trackingUrl(resolveTrackingUrl(promotion))
                .streamingLinks(toStreamingLinks(promotionId, streamingClickCount))
                .realtimeStats(PromotionAnalysisPageResponse.RealtimeStats.builder()
                        .trackingClickCount(trackingClickCount)
                        .streamingClickCount(streamingClickCount)
                        .build())
                .diagnosisSection(toDiagnosisSection(promotionId))
                .build();
    }

    private List<PromotionAnalysisPageResponse.StreamingLink> toStreamingLinks(
            Long promotionId,
            long totalStreamingClickCount
    ) {
        List<PromotionStreamingLink> links =
                promotionStreamingLinkRepository.findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId);

        return links.stream()
                .map(link -> {
                    long clickCount =
                            promotionStreamingClickRepository.countByStreamingLinkId(link.getId());

                    return PromotionAnalysisPageResponse.StreamingLink.builder()
                            .streamingCode(link.getStreamingCode())
                            .url(link.getOriginalUrl())
                            .clickUrl(resolveStreamingClickUrl(link))
                            .displayOrder(link.getDisplayOrder())
                            .clickCount(clickCount)
                            .clickShareRate(percent(clickCount, totalStreamingClickCount))
                            .build();
                })
                .toList();
    }

    private PromotionAnalysisPageResponse.DiagnosisSection toDiagnosisSection(Long promotionId) {
        List<PromotionDiagnosis> diagnoses =
                promotionDiagnosisRepository.findByMusicPromotion_IdOrderByDiagnosedAtDesc(promotionId);

        if (!diagnoses.isEmpty()) {
            return PromotionAnalysisPageResponse.DiagnosisSection.builder()
                    .status("COMPLETED")
                    .diagnosisCards(
                            diagnoses.stream()
                                    .map(this::toDiagnosisCard)
                                    .toList()
                    )
                    .build();
        }

        PromotionAnalysisJob latestJob = promotionAnalysisJobRepository
                .findTopByPromotion_IdOrderByCreatedAtDesc(promotionId)
                .orElse(null);

        if (latestJob == null) {
            return PromotionAnalysisPageResponse.DiagnosisSection.builder()
                    .status("NOT_STARTED")
                    .diagnosisCards(List.of())
                    .build();
        }

        if (latestJob.getStatus() == AnalysisJobStatus.RUNNING) {
            return PromotionAnalysisPageResponse.DiagnosisSection.builder()
                    .status("RUNNING")
                    .diagnosisCards(List.of())
                    .build();
        }

        if (latestJob.getStatus() == AnalysisJobStatus.FAILED) {
            return PromotionAnalysisPageResponse.DiagnosisSection.builder()
                    .status("FAILED")
                    .diagnosisCards(List.of())
                    .build();
        }

        return PromotionAnalysisPageResponse.DiagnosisSection.builder()
                .status("NOT_STARTED")
                .diagnosisCards(List.of())
                .build();
    }

    private PromotionAnalysisPageResponse.DiagnosisCard toDiagnosisCard(PromotionDiagnosis diagnosis) {
        PromotionActionPlan firstAction = diagnosis.getActionPlans()
                .stream()
                .min(Comparator.comparing(
                        PromotionActionPlan::getActionOrder,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .orElse(null);

        return PromotionAnalysisPageResponse.DiagnosisCard.builder()
                .diagnosisId(diagnosis.getDiagnosisId())
                .diagnosedDate(formatDate(diagnosis.getDiagnosedAt()))
                .bottleneckType(defaultIfBlank(diagnosis.getBottleneckType(), "진단 결과"))
                .headline(defaultIfBlank(diagnosis.getHeadline(), "홍보 흐름을 개선할 수 있어요."))
                .actionTitle(firstAction == null
                        ? "바로 적용할 액션을 확인해보세요."
                        : defaultIfBlank(firstAction.getTitle(), "바로 적용할 액션을 확인해보세요."))
                .unread(diagnosis.isUnread())
                .build();
    }

    private String resolveTrackingUrl(MusicPromotion promotion) {
        if (promotion.getPromotionTrackingLink() == null) {
            return null;
        }

        return promotion.getPromotionTrackingLink().getTrackingUrl();
    }

    private String resolveStreamingClickUrl(PromotionStreamingLink link) {
        if (link.getRedirectUrl() != null && !link.getRedirectUrl().isBlank()) {
            return link.getRedirectUrl();
        }

        return link.getOriginalUrl();
    }

    private double percent(long numerator, long denominator) {
        if (denominator == 0) {
            return 0.0;
        }

        double value = (double) numerator / denominator * 100;
        return Math.round(value * 100.0) / 100.0;
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.format(DateTimeFormatter.ofPattern("yy.MM.dd"));
    }

    private void validateOwner(MusicPromotion promotion, Long musicianId) {
        if (!promotion.getMusician().getId().equals(musicianId)) {
            throw new IllegalArgumentException("본인의 프로모션만 조회할 수 없습니다.");
        }
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}