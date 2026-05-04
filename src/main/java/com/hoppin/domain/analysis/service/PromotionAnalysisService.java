package com.hoppin.domain.analysis.service;

import com.hoppin.domain.InstagramMediaInsight.entity.InstagramMediaInsight;
import com.hoppin.domain.InstagramMediaInsight.entity.MusicianInstagramInsightSnapshot;
import com.hoppin.domain.InstagramMediaInsight.repository.InstagramMediaInsightRepository;
import com.hoppin.domain.InstagramMediaInsight.repository.MusicianInstagramInsightSnapshotRepository;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.entity.PromotionDiagnosisMetric;
import com.hoppin.domain.analysis.enumtype.DiagnosisStatus;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.infra.ai.dto.response.ActionCardDto;
import com.hoppin.infra.ai.dto.request.AnalysisCreateRequest;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.dto.response.DiagnosisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionAnalysisService {

    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final MusicPromotionRepository musicPromotionRepository;

    private final MusicianInstagramInsightSnapshotRepository snapshotRepository;
    private final InstagramMediaInsightRepository mediaInsightRepository;
    private final PromotionTrackingClickRepository trackingClickRepository;

    public AnalysisRequestDto buildAnalysisRequest(
            Long musicianId,
            Long promotionId,
            AnalysisCreateRequest request
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        LocalDateTime sinceDateTime = request.getSinceDate().atStartOfDay();
        String sinceDate = request.getSinceDate().toString();

        MusicianInstagramInsightSnapshot snapshot = snapshotRepository
                .findTopByMusicianIdOrderByCreatedAtDesc(musicianId)
                .orElseThrow(() -> new IllegalArgumentException("인스타그램 인사이트 스냅샷이 없습니다."));

        List<InstagramMediaInsight> mediaInsights =
                mediaInsightRepository.findBySnapshotIdAndTimestampGreaterThanEqual(
                        snapshot.getId(),
                        sinceDate
                );

        List<PromotionTrackingClick> clicks =
                trackingClickRepository.findByPromotionIdAndClickedAtAfter(
                        promotionId,
                        sinceDateTime
                );

        int contentCount = mediaInsights.size();
        int reachCount = sumReach(mediaInsights);
        int shareCount = sumShare(mediaInsights);
        int profileVisitCount = sumProfileVisit(mediaInsights);
        int linkClickCount = clicks.size();

        AnalysisRequestDto aiRequest = new AnalysisRequestDto();

        aiRequest.setSinceDate(sinceDate);
        aiRequest.setMainPainPoint(request.getMainPainPoint());
        aiRequest.setMainResourceConstraint(request.getMainResourceConstraint());

        aiRequest.setPromoLink(musicPromotion.getPromotionTrackingLink().getTrackingUrl());

        aiRequest.setContentCount(contentCount);
        aiRequest.setReachCount(reachCount);
        aiRequest.setShareCount(shareCount);
        aiRequest.setProfileVisitCount(profileVisitCount);
        aiRequest.setLinkClickCount(linkClickCount);

        aiRequest.setChannelClicks(toChannelClickSummaries(clicks));
        aiRequest.setTopCandidatePosts(toTopPosts(mediaInsights));
        aiRequest.setLowCandidatePosts(toLowPosts(mediaInsights));

        LocalDate today = LocalDate.now();

        String analysisMode = today.isBefore(musicPromotion.getReleaseDate())
                ? "PRE_CAMPAIGN"
                : "POST_CAMPAIGN";

        aiRequest.setAnalysisMode(analysisMode);
        aiRequest.setReleaseDate(musicPromotion.getReleaseDate().toString());

        return aiRequest;
    }

    public Long saveAnalysisResult(
            Long musicianId,
            Long promotionId,
            AnalysisResponseDto responseDto
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        DiagnosisDto diagnosisDto = responseDto.getDiagnosis();

        if (diagnosisDto == null) {
            throw new IllegalArgumentException("AI 분석 결과 diagnosis가 없습니다.");
        }

        PromotionDiagnosis diagnosis = PromotionDiagnosis.builder()
                .musicPromotion(musicPromotion)
                .headline(responseDto.getHeadline())
                .bottleneckType(diagnosisDto.getBottleneckType())
                .highlightSection(diagnosisDto.getHighlightSection())
                .interpretation(diagnosisDto.getInterpretation())
                .status(DiagnosisStatus.COMPLETED)
                .diagnosedAt(LocalDateTime.now())
                .build();

        PromotionDiagnosisMetric metric = PromotionDiagnosisMetric.builder()
                .shareCount(diagnosisDto.getShareCount())
                .profileVisitCount(diagnosisDto.getProfileVisitCount())
                .linkClickCount(diagnosisDto.getLinkClickCount())
                .build();

        diagnosis.assignMetric(metric);

        List<ActionCardDto> actions = responseDto.getActions();

        if (actions != null) {
            int order = 1;

            for (ActionCardDto actionDto : actions) {
                PromotionActionPlan actionPlan = PromotionActionPlan.builder()
                        .actionOrder(order++)
                        .title(actionDto.getTitle())
                        .reason(actionDto.getReason())
                        .metric(actionDto.getMetric())
                        .example(actionDto.getExample())
                        .build();

                diagnosis.addActionPlan(actionPlan);
            }
        }

        return promotionDiagnosisRepository.save(diagnosis).getDiagnosisId();
    }

    private void validateOwner(MusicPromotion musicPromotion, Long musicianId) {
        if (!musicPromotion.getMusician().getId().equals(musicianId)) {
            throw new IllegalArgumentException("본인의 프로모션만 분석할 수 있습니다.");
        }
    }

    private int sumReach(List<InstagramMediaInsight> mediaInsights) {
        return mediaInsights.stream()
                .mapToInt(m -> toInt(m.getReachCount()))
                .sum();
    }

    private int sumShare(List<InstagramMediaInsight> mediaInsights) {
        return mediaInsights.stream()
                .mapToInt(m -> toInt(m.getShareCount()))
                .sum();
    }

    private int sumProfileVisit(List<InstagramMediaInsight> mediaInsights) {
        return mediaInsights.stream()
                .mapToInt(m -> toInt(m.getProfileVisitCount()))
                .sum();
    }

    private int toInt(Long value) {
        return value == null ? 0 : value.intValue();
    }

    private List<AnalysisRequestDto.ChannelClickSummary> toChannelClickSummaries(
            List<PromotionTrackingClick> clicks
    ) {
        return clicks.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getTrackingLink().getChannel().name(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    AnalysisRequestDto.ChannelClickSummary dto =
                            new AnalysisRequestDto.ChannelClickSummary();

                    dto.setChannel(entry.getKey());
                    dto.setClickCount(entry.getValue().intValue());

                    return dto;
                })
                .toList();
    }

    private List<AnalysisRequestDto.PostMetricSummary> toTopPosts(
            List<InstagramMediaInsight> mediaInsights
    ) {
        return mediaInsights.stream()
                .sorted(Comparator.comparingLong(this::score).reversed())
                .limit(3)
                .map(this::toPostMetricSummary)
                .toList();
    }

    private List<AnalysisRequestDto.PostMetricSummary> toLowPosts(
            List<InstagramMediaInsight> mediaInsights
    ) {
        return mediaInsights.stream()
                .sorted(Comparator.comparingLong(this::score))
                .limit(3)
                .map(this::toPostMetricSummary)
                .toList();
    }

    private long score(InstagramMediaInsight insight) {
        long reach = insight.getReachCount() == null ? 0 : insight.getReachCount();
        long share = insight.getShareCount() == null ? 0 : insight.getShareCount();
        long profileVisit = insight.getProfileVisitCount() == null ? 0 : insight.getProfileVisitCount();

        return reach + (share * 3) + (profileVisit * 5);
    }

    private AnalysisRequestDto.PostMetricSummary toPostMetricSummary(InstagramMediaInsight insight) {
        AnalysisRequestDto.PostMetricSummary dto = new AnalysisRequestDto.PostMetricSummary();

        dto.setMediaId(insight.getMediaId());
        dto.setCaption(insight.getCaption());
        dto.setMediaType(insight.getMediaType());
        dto.setPermalink(insight.getPermalink());
        dto.setTimestamp(insight.getTimestamp());

        dto.setReachCount(toInt(insight.getReachCount()));
        dto.setShareCount(toInt(insight.getShareCount()));
        dto.setProfileVisitCount(toInt(insight.getProfileVisitCount()));

        return dto;
    }
}