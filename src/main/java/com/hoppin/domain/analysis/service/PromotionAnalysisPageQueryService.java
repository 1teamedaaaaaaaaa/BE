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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private static final int DIAGNOSIS_PAGE_SIZE = 10;

    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionTrackingClickRepository promotionTrackingClickRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final PromotionAnalysisJobRepository promotionAnalysisJobRepository;

    public PromotionAnalysisPageResponse getAnalysisPage(
            Long musicianId,
            Long promotionId,
            int page
    ) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(promotion, musicianId);

        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(safePage, DIAGNOSIS_PAGE_SIZE);

        long trackingClickCount =
                promotionTrackingClickRepository.countByPromotionId(promotionId);

        long streamingClickCount =
                promotionStreamingClickRepository.countByPromotionId(promotionId);

        DiagnosisPageResult diagnosisPageResult =
                toDiagnosisPageResult(promotionId, pageable);

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
                .diagnosis(diagnosisPageResult.items())
                .diagnosisPage(diagnosisPageResult.pageInfo())
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

    private DiagnosisPageResult toDiagnosisPageResult(
            Long promotionId,
            Pageable pageable
    ) {
        Page<PromotionDiagnosis> diagnosisPage =
                promotionDiagnosisRepository.findPageByMusicPromotion_IdOrderByDiagnosedAtDesc(
                        promotionId,
                        pageable
                );

        if (diagnosisPage.hasContent()) {
            List<PromotionAnalysisPageResponse.AnalysisDiagnosisItem> items =
                    diagnosisPage.getContent()
                            .stream()
                            .map(this::toCompletedDiagnosis)
                            .toList();

            return new DiagnosisPageResult(
                    items,
                    toPageInfo(diagnosisPage)
            );
        }

        // 진단 결과가 없는 상태에서 page=1 이상이면 빈 배열로 내려줌
        if (pageable.getPageNumber() > 0) {
            return new DiagnosisPageResult(
                    List.of(),
                    PromotionAnalysisPageResponse.DiagnosisPage.builder()
                            .page(pageable.getPageNumber())
                            .size(pageable.getPageSize())
                            .totalElements(0)
                            .totalPages(0)
                            .hasNext(false)
                            .build()
            );
        }

        PromotionAnalysisJob latestJob = promotionAnalysisJobRepository
                .findTopByPromotion_IdOrderByCreatedAtDesc(promotionId)
                .orElse(null);

        String status = resolveEmptyDiagnosisStatus(latestJob);

        return new DiagnosisPageResult(
                List.of(toEmptyDiagnosis(status)),
                PromotionAnalysisPageResponse.DiagnosisPage.builder()
                        .page(pageable.getPageNumber())
                        .size(pageable.getPageSize())
                        .totalElements(1)
                        .totalPages(1)
                        .hasNext(false)
                        .build()
        );
    }

    private String resolveEmptyDiagnosisStatus(PromotionAnalysisJob latestJob) {
        if (latestJob == null) {
            return "PENDING";
        }

        if (latestJob.getStatus() == AnalysisJobStatus.RUNNING) {
            return "RUNNING";
        }

        if (latestJob.getStatus() == AnalysisJobStatus.FAILED) {
            return "FAILED";
        }

        return "PENDING";
    }

    private PromotionAnalysisPageResponse.AnalysisDiagnosisItem toCompletedDiagnosis(
            PromotionDiagnosis diagnosis
    ) {
        PromotionActionPlan firstAction = diagnosis.getActionPlans()
                .stream()
                .min(Comparator.comparing(
                        PromotionActionPlan::getActionOrder,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .orElse(null);

        return PromotionAnalysisPageResponse.AnalysisDiagnosisItem.builder()
                .status("COMPLETED")
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

    private PromotionAnalysisPageResponse.AnalysisDiagnosisItem toEmptyDiagnosis(String status) {
        return PromotionAnalysisPageResponse.AnalysisDiagnosisItem.builder()
                .status(status)
                .diagnosisId(null)
                .diagnosedDate(null)
                .bottleneckType(null)
                .headline(null)
                .actionTitle(null)
                .unread(false)
                .build();
    }

    private PromotionAnalysisPageResponse.DiagnosisPage toPageInfo(
            Page<PromotionDiagnosis> page
    ) {
        return PromotionAnalysisPageResponse.DiagnosisPage.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
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

    private record DiagnosisPageResult(
            List<PromotionAnalysisPageResponse.AnalysisDiagnosisItem> items,
            PromotionAnalysisPageResponse.DiagnosisPage pageInfo
    ) {
    }
}