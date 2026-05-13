package com.hoppin.domain.analysis.service;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionCalculatedMetrics;
import com.hoppin.domain.analysis.enumtype.AnalysisMode;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.global.exception.ResourceNotFoundException;
import com.hoppin.infra.ai.dto.response.CalculatedMetricsDto;
import com.hoppin.infra.crawling.entity.PromotionAnalysisCrawledPost;
import com.hoppin.infra.crawling.entity.PromotionAnalysisJob;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.entity.PromotionDiagnosisMetric;
import com.hoppin.domain.analysis.enumtype.DiagnosisStatus;
import com.hoppin.infra.crawling.repository.PromotionAnalysisCrawledPostRepository;
import com.hoppin.infra.crawling.repository.PromotionAnalysisJobRepository;
import com.hoppin.domain.mypage.service.MyPageSseService;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.ActionCardDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.dto.response.DiagnosisDto;
import com.hoppin.infra.mail.dto.AnalysisMailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionAnalysisService {

    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionAnalysisJobRepository promotionAnalysisJobRepository;
    private final PromotionAnalysisCrawledPostRepository promotionAnalysisCrawledPostRepository;
    private final MyPageSseService myPageSseService;

    private final PromotionTrackingLinkRepository promotionTrackingLinkRepository;
    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final PromotionTrackingClickRepository promotionTrackingClickRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;

    /**
     * n8n internal API용
     * promotionId + analysisJobId를 기준으로 정확히 해당 Job의 크롤링 데이터를 AI 요청값으로 조립
     */
    // n8n internal API용
    public AnalysisRequestDto buildAnalysisRequestForJob(
            Long promotionId,
            Long analysisJobId
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        PromotionAnalysisJob job = promotionAnalysisJobRepository.findById(analysisJobId)
                .orElseThrow(() -> new IllegalArgumentException("분석 작업이 존재하지 않습니다. id=" + analysisJobId));

        if (!job.getPromotion().getId().equals(promotionId)) {
            throw new IllegalArgumentException("프로모션과 분석 작업이 일치하지 않습니다.");
        }

        return buildAnalysisRequestDto(musicPromotion, job);
    }

    /**
     * 로그인 사용자 API용
     * 사용자 본인 프로모션인지 검증 후 최신 Job 기준으로 AI 요청값 조립
     */
    // 로그인 사용자 API용
    public AnalysisRequestDto buildLatestAnalysisRequest(
            Long musicianId,
            Long promotionId
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        PromotionAnalysisJob job = promotionAnalysisJobRepository
                .findTopByPromotion_IdOrderByCreatedAtDesc(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("분석 작업이 존재하지 않습니다. promotionId=" + promotionId));

        return buildAnalysisRequestDto(musicPromotion, job);
    }

    /**
     * AI 요청 DTO 공통 조립 로직
     */
    private AnalysisRequestDto buildAnalysisRequestDto(
            MusicPromotion musicPromotion,
            PromotionAnalysisJob job
    ) {
        List<PromotionAnalysisCrawledPost> crawledPosts =
                promotionAnalysisCrawledPostRepository.findByAnalysisJobIdOrderByCreatedAtDesc(job.getId());

        int contentCount = job.getContentCount() != null
                ? job.getContentCount()
                : crawledPosts.size();

        int totalLikeCount = job.getTotalLikeCount() != null
                ? job.getTotalLikeCount()
                : crawledPosts.stream()
                  .mapToInt(post -> post.getLikeCount() == null ? 0 : post.getLikeCount())
                  .sum();

        int totalCommentCount = job.getTotalCommentCount() != null
                ? job.getTotalCommentCount()
                : crawledPosts.stream()
                  .mapToInt(post -> post.getCommentCount() == null ? 0 : post.getCommentCount())
                  .sum();

        return AnalysisRequestDto.builder()
                .promotionId(getPromotionId(musicPromotion))
                .analysisJobId(job.getId())
                .sinceDate(job.getSinceDate().toString())
                .releaseDate(musicPromotion.getReleaseDate() == null ? null : musicPromotion.getReleaseDate().toString())
                .instagramUsername(job.getInstagramUsername())
                .analysisMode(resolveAnalysisMode(musicPromotion).name())
                .promoLink(resolvePromoLink(musicPromotion))
                .mainPainPoint(job.getMainPainPoint())
                .mainResourceConstraint(job.getMainResourceConstraint())
                .instagramSummary(AnalysisRequestDto.InstagramSummary.builder()
                        .contentCount(contentCount)
                        .followerCount(job.getFollowerCount() == null ? 0 : job.getFollowerCount())
                        .totalLikeCount(totalLikeCount)
                        .totalCommentCount(totalCommentCount)
                        .build())
                .linkClickSummary(buildLinkClickSummary(musicPromotion))
                .posts(toCrawledPostSummaries(crawledPosts))
                .build();
    }

    /**
     * n8n internal API용 저장
     * 사용자 Authentication 없이 promotionId만으로 저장
     */
    public Long saveAnalysisResult(
            Long promotionId,
            Long analysisJobId,
            AnalysisResponseDto responseDto
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        PromotionAnalysisJob job = promotionAnalysisJobRepository.findById(analysisJobId)
                .orElseThrow(() -> new IllegalArgumentException("분석 작업이 존재하지 않습니다. id=" + analysisJobId));

        if (!job.getPromotion().getId().equals(promotionId)) {
            throw new IllegalArgumentException("프로모션과 분석 작업이 일치하지 않습니다.");
        }

        return saveAnalysisResultInternal(musicPromotion, job, responseDto);
    }

    /**
     * 로그인 사용자 API용 저장
     * 본인 프로모션인지 검증 후 저장
     */
    public Long saveAnalysisResult(
            Long musicianId,
            Long promotionId,
            Long analysisJobId,
            AnalysisResponseDto responseDto
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        PromotionAnalysisJob job = promotionAnalysisJobRepository.findById(analysisJobId)
                .orElseThrow(() -> new IllegalArgumentException("분석 작업이 존재하지 않습니다. id=" + analysisJobId));

        if (!job.getPromotion().getId().equals(promotionId)) {
            throw new IllegalArgumentException("프로모션과 분석 작업이 일치하지 않습니다.");
        }

        return saveAnalysisResultInternal(musicPromotion, job, responseDto);
    }

    public void markAllDiagnosesAsRead(
            Long musicianId,
            Long promotionId
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        List<PromotionDiagnosis> diagnoses = promotionDiagnosisRepository
                .findByMusicPromotion_IdOrderByDiagnosedAtDesc(promotionId);

        if (diagnoses.isEmpty()) {
            throw new IllegalArgumentException("진단 결과가 존재하지 않습니다. promotionId=" + promotionId);
        }

        diagnoses.stream()
                .filter(PromotionDiagnosis::isUnread)
                .forEach(PromotionDiagnosis::markRead);

        myPageSseService.publishPromotionUpdatedAfterCommit(musicianId, promotionId);
    }

    /**
     * AI 분석 결과 공통 저장 로직
     */
    private Long saveAnalysisResultInternal(
            MusicPromotion musicPromotion,
            PromotionAnalysisJob job,
            AnalysisResponseDto responseDto
    ) {
        DiagnosisDto diagnosisDto = responseDto.getDiagnosis();

        if (diagnosisDto == null) {
            throw new IllegalArgumentException("AI 분석 결과 diagnosis가 없습니다.");
        }

        PromotionDiagnosis diagnosis = PromotionDiagnosis.builder()
                .musicPromotion(musicPromotion)
                .sinceDate(job.getSinceDate())
                .headline(responseDto.getHeadline())
                .bottleneckType(diagnosisDto.getBottleneckType())
                .highlightSection(diagnosisDto.getHighlightSection())
                .interpretation(diagnosisDto.getInterpretation())
                .status(DiagnosisStatus.COMPLETED)
                .diagnosedAt(LocalDateTime.now())
                .build();

        PromotionDiagnosisMetric metric = PromotionDiagnosisMetric.builder()
                .contentCount(toZeroIfNull(diagnosisDto.getContentCount()))
                .followerCount(toZeroIfNull(diagnosisDto.getFollowerCount()))
                .totalLikeCount(toZeroIfNull(diagnosisDto.getTotalLikeCount()))
                .totalCommentCount(toZeroIfNull(diagnosisDto.getTotalCommentCount()))
                .trackingLinkClickCount(toZeroIfNull(diagnosisDto.getTrackingLinkClickCount()))
                .streamingLinkClickCount(toZeroIfNull(diagnosisDto.getStreamingLinkClickCount()))
                .totalLinkClickCount(toZeroIfNull(diagnosisDto.getTotalLinkClickCount()))
                .build();

        CalculatedMetricsDto calculatedMetricsDto = responseDto.getCalculatedMetrics();

        PromotionCalculatedMetrics calculatedMetrics = PromotionCalculatedMetrics.builder()
                .avgLikePerPost(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getAvgLikePerPost()))
                .avgCommentPerPost(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getAvgCommentPerPost()))
                .commentRateByLike(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getCommentRateByLike()))
                .streamingClickShare(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getStreamingClickShare()))
                .followerEngagementRate(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getFollowerEngagementRate()))
                .promoClickRateByEngagement(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getPromoClickRateByEngagement()))
                .streamingClickRateByPromoClick(toZeroIfNull(calculatedMetricsDto == null ? null : calculatedMetricsDto.getStreamingClickRateByPromoClick()))
                .build();

        diagnosis.assignCalculatedMetrics(calculatedMetrics);

        diagnosis.assignMetric(metric);

        List<ActionCardDto> actions = responseDto.getActions();

        if (actions != null) {
            int order = 1;

            for (ActionCardDto actionDto : actions) {
                PromotionActionPlan actionPlan = PromotionActionPlan.builder()
                        .actionOrder(order++)
                        .title(actionDto.getTitle())
                        .metric(actionDto.getMetric())
                        .details(actionDto.getDetails())
                        .build();

                diagnosis.addActionPlan(actionPlan);
            }
        }

        Long diagnosisId = promotionDiagnosisRepository.save(diagnosis).getDiagnosisId();
        myPageSseService.publishPromotionUpdatedAfterCommit(
                musicPromotion.getMusician().getId(),
                musicPromotion.getId()
        );
        return diagnosisId;
    }

    private double toZeroIfNull(Double value) {
        return value == null ? 0.0 : value;
    }

    private void validateOwner(MusicPromotion musicPromotion, Long musicianId) {
        if (!musicPromotion.getMusician().getId().equals(musicianId)) {
            throw new IllegalArgumentException("본인의 프로모션만 분석할 수 있습니다.");
        }
    }

    private AnalysisMode resolveAnalysisMode(MusicPromotion musicPromotion) {
        if (musicPromotion.getReleaseDate() == null) {
            return AnalysisMode.POST_CAMPAIGN;
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return today.isBefore(musicPromotion.getReleaseDate())
                ? AnalysisMode.PRE_CAMPAIGN
                : AnalysisMode.POST_CAMPAIGN;
    }

    private String resolvePromoLink(MusicPromotion musicPromotion) {
        if (musicPromotion.getPromotionTrackingLink() == null) {
            return null;
        }

        return musicPromotion.getPromotionTrackingLink().getTrackingUrl();
    }

    private List<AnalysisRequestDto.CrawledPostSummary> toCrawledPostSummaries(
            List<PromotionAnalysisCrawledPost> crawledPosts
    ) {
        return crawledPosts.stream()
                .map(post -> AnalysisRequestDto.CrawledPostSummary.builder()
                        .mediaId(post.getMediaId())
                        .caption(post.getCaption())
                        .mediaType(post.getMediaType())
                        .permalink(post.getPermalink())
                        .timestamp(post.getTimestamp())
                        .likeCount(post.getLikeCount() == null ? 0 : post.getLikeCount())
                        .commentCount(post.getCommentCount() == null ? 0 : post.getCommentCount())
                        .build())
                .toList();
    }

    private AnalysisRequestDto.LinkClickSummary buildLinkClickSummary(MusicPromotion musicPromotion) {
        Long promotionId = musicPromotion.getId();

        List<AnalysisRequestDto.TrackingLinkClickSummary> trackingLinks =
                promotionTrackingLinkRepository.findByPromotionId(promotionId)
                        .stream()
                        .map(link -> {
                            long clickCount = promotionTrackingClickRepository
                                    .countByTrackingLinkId(link.getId());

                            return AnalysisRequestDto.TrackingLinkClickSummary.builder()
                                    .channel(link.getChannel().name())
                                    .url(link.getTrackingUrl())
                                    .clickCount(clickCount)
                                    .build();
                        })
                        .toList();

        List<AnalysisRequestDto.StreamingLinkClickSummary> streamingLinks =
                promotionStreamingLinkRepository.findByPromotionIdAndActiveTrueOrderByDisplayOrderAsc(promotionId)
                        .stream()
                        .map(link -> {
                            long clickCount = promotionStreamingClickRepository
                                    .countByStreamingLinkId(link.getId());

                            return AnalysisRequestDto.StreamingLinkClickSummary.builder()
                                    .streamingCode(link.getStreamingCode())
                                    .url(link.getOriginalUrl())
                                    .clickCount(clickCount)
                                    .build();
                        })
                        .toList();

        long trackingTotal = trackingLinks.stream()
                .mapToLong(AnalysisRequestDto.TrackingLinkClickSummary::getClickCount)
                .sum();

        long streamingTotal = streamingLinks.stream()
                .mapToLong(AnalysisRequestDto.StreamingLinkClickSummary::getClickCount)
                .sum();

        return AnalysisRequestDto.LinkClickSummary.builder()
                .trackingLinkTotalClickCount(trackingTotal)
                .streamingLinkTotalClickCount(streamingTotal)
                .trackingLinks(trackingLinks)
                .streamingLinks(streamingLinks)
                .build();
    }

    private int toZeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }

    private Long getPromotionId(MusicPromotion musicPromotion) {
        return musicPromotion.getId();
    }

    @Transactional(readOnly = true)
    public AnalysisMailInfo getAnalysisMailInfo(Long promotionId, Long analysisJobId) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("홍보 정보를 찾을 수 없습니다."));

        Musician musician = promotion.getMusician();

        String detailPageUrl = "https://www.musicpeak.site/album/analysis/"
                + promotionId;

        String reportImageUrl = promotion.getImageUrl();

        return new AnalysisMailInfo(
                musician.getEmail(),
                promotion.getActivityName(),
                promotion.getSongTitle(),
                reportImageUrl,
                detailPageUrl
        );
    }
}
