package com.hoppin.domain.analysis.service;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionAnalysisCrawledPost;
import com.hoppin.domain.analysis.entity.PromotionAnalysisJob;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.entity.PromotionDiagnosisMetric;
import com.hoppin.domain.analysis.enumtype.DiagnosisStatus;
import com.hoppin.domain.analysis.repository.PromotionAnalysisCrawledPostRepository;
import com.hoppin.domain.analysis.repository.PromotionAnalysisJobRepository;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.infra.ai.dto.request.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.response.ActionCardDto;
import com.hoppin.infra.ai.dto.response.AnalysisResponseDto;
import com.hoppin.infra.ai.dto.response.DiagnosisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionAnalysisService {

    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionAnalysisJobRepository promotionAnalysisJobRepository;
    private final PromotionAnalysisCrawledPostRepository promotionAnalysisCrawledPostRepository;

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
                .analysisMode(resolveAnalysisMode(musicPromotion))
                .promoLink(resolvePromoLink(musicPromotion))
                .mainPainPoint(job.getMainPainPoint())
                .mainResourceConstraint(job.getMainResourceConstraint())
                .instagramSummary(AnalysisRequestDto.InstagramSummary.builder()
                        .contentCount(contentCount)
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
            AnalysisResponseDto responseDto
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        return saveAnalysisResultInternal(musicPromotion, responseDto);
    }

    /**
     * 로그인 사용자 API용 저장
     * 본인 프로모션인지 검증 후 저장
     */
    public Long saveAnalysisResult(
            Long musicianId,
            Long promotionId,
            AnalysisResponseDto responseDto
    ) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(musicPromotion, musicianId);

        return saveAnalysisResultInternal(musicPromotion, responseDto);
    }

    /**
     * AI 분석 결과 공통 저장 로직
     */
    private Long saveAnalysisResultInternal(
            MusicPromotion musicPromotion,
            AnalysisResponseDto responseDto
    ) {
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
                .contentCount(toZeroIfNull(diagnosisDto.getContentCount()))
                .totalLikeCount(toZeroIfNull(diagnosisDto.getTotalLikeCount()))
                .totalCommentCount(toZeroIfNull(diagnosisDto.getTotalCommentCount()))
                .trackingLinkClickCount(toZeroIfNull(diagnosisDto.getTrackingLinkClickCount()))
                .streamingLinkClickCount(toZeroIfNull(diagnosisDto.getStreamingLinkClickCount()))
                .totalLinkClickCount(toZeroIfNull(diagnosisDto.getTotalLinkClickCount()))
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

    private String resolveAnalysisMode(MusicPromotion musicPromotion) {
        if (musicPromotion.getReleaseDate() == null) {
            return "POST_CAMPAIGN";
        }

        LocalDate today = LocalDate.now();

        return today.isBefore(musicPromotion.getReleaseDate())
                ? "PRE_CAMPAIGN"
                : "POST_CAMPAIGN";
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
        /*
         * TODO:
         * 현재는 클릭 수를 0으로 넣는 임시 버전.
         * PromotionTrackingClickRepository, PromotionStreamingClickRepository를 주입하면
         * 여기서 실제 클릭 수를 계산해서 넣으면 됨.
         */
        List<AnalysisRequestDto.TrackingLinkClickSummary> trackingLinks = List.of();
        List<AnalysisRequestDto.StreamingLinkClickSummary> streamingLinks = List.of();

        int trackingTotal = trackingLinks.stream()
                .mapToInt(AnalysisRequestDto.TrackingLinkClickSummary::getClickCount)
                .sum();

        int streamingTotal = streamingLinks.stream()
                .mapToInt(AnalysisRequestDto.StreamingLinkClickSummary::getClickCount)
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

    public String getPromotionOwnerEmail(Long promotionId) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다."));

        return promotion.getMusician().getEmail();
    }
}