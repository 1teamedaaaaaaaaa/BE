package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionTitleItemResponse;
import com.hoppin.infra.crawling.entity.PromotionAnalysisJob;
import com.hoppin.infra.crawling.repository.PromotionAnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MusicPromotionRepositoryJpaImpl implements MusicPromotionRepository {

    private final MusicPromotionJpaRepository musicPromotionJpaRepository;
    private final PromotionTrackingClickRepository promotionTrackingClickRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;
    private final PromotionAnalysisJobRepository promotionAnalysisJobRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;

    @Override
    public MusicPromotion save(MusicPromotion promotion) {
        return musicPromotionJpaRepository.save(promotion);
    }

    @Override
    public Optional<MusicPromotion> findById(Long promotionId) {
        return musicPromotionJpaRepository.findById(promotionId);
    }

    @Override
    public List<MusicPromotion> findByMusicianId(Long musicianId) {
        return musicPromotionJpaRepository.findByMusicianId(musicianId);
    }

    @Override
    public boolean existsById(Long promotionId) {
        return musicPromotionJpaRepository.existsById(promotionId);
    }

    @Override
    public Page<MyPagePromotionItemResponse> findMyPagePromotions(
            Long musicianId,
            String keyword,
            Pageable pageable
    ) {
        List<MyPagePromotionItemResponse> sortedPromotions = findMyPagePromotions(musicianId, keyword);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedPromotions.size());
        List<MyPagePromotionItemResponse> pageContent =
                start >= sortedPromotions.size() ? List.of() : sortedPromotions.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedPromotions.size());
    }

    @Override
    public List<MyPagePromotionItemResponse> findMyPagePromotions(Long musicianId, String keyword) {
        return musicPromotionJpaRepository.findMyPagePromotions(musicianId, keyword).stream()
                .map(this::toMyPagePromotionItemResponse)
                .sorted(myPagePromotionComparator())
                .toList();
    }

    @Override
    public Page<MyPagePromotionTitleItemResponse> findMyPagePromotionTitles(Long musicianId, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return musicPromotionJpaRepository.findMyPagePromotionTitles(musicianId, pageable)
                .map(this::toMyPagePromotionTitleItemResponse);
    }

    @Override
    public Optional<MyPagePromotionItemResponse> findMyPagePromotion(
            Long musicianId,
            Long promotionId
    ) {
        return musicPromotionJpaRepository.findById(promotionId)
                .filter(promotion -> promotion.getMusician().getId().equals(musicianId))
                .map(this::toMyPagePromotionItemResponse);
    }

    @Override
    public void delete(MusicPromotion promotion) {
        musicPromotionJpaRepository.delete(promotion);
    }

    private MyPagePromotionItemResponse toMyPagePromotionItemResponse(MusicPromotion promotion) {
        long trackingLinkClickCount =
                promotionTrackingClickRepository.countByPromotionId(promotion.getId());
        long streamingLinkClickCount =
                promotionStreamingClickRepository.countByPromotionId(promotion.getId());

        PromotionAnalysisJob latestJob = promotionAnalysisJobRepository
                .findTopByPromotion_IdOrderByCreatedAtDesc(promotion.getId())
                .orElse(null);

        PromotionDiagnosis latestDiagnosis = promotionDiagnosisRepository
                .findTopByMusicPromotion_IdOrderByDiagnosedAtDesc(promotion.getId())
                .orElse(null);

        MyPagePromotionItemResponse.AnalysisSummary analysisSummary = null;
        LocalDateTime lastActivityAt = promotion.getUpdatedAt().isAfter(promotion.getCreatedAt())
                ? promotion.getUpdatedAt()
                : promotion.getCreatedAt();

        if (latestJob != null || latestDiagnosis != null) {
            analysisSummary = new MyPagePromotionItemResponse.AnalysisSummary(
                    latestJob == null ? null : latestJob.getStatus().name(),
                    latestDiagnosis == null ? null : latestDiagnosis.getBottleneckType(),
                    latestDiagnosis != null && latestDiagnosis.isUnread(),
                    latestDiagnosis == null ? null : latestDiagnosis.getDiagnosedAt()
            );
        }

        if (latestDiagnosis != null) {
            if (latestDiagnosis.getDiagnosedAt() != null && latestDiagnosis.getDiagnosedAt().isAfter(lastActivityAt)) {
                lastActivityAt = latestDiagnosis.getDiagnosedAt();
            }
            if (latestDiagnosis.getReadAt() != null && latestDiagnosis.getReadAt().isAfter(lastActivityAt)) {
                lastActivityAt = latestDiagnosis.getReadAt();
            }
        }

        return new MyPagePromotionItemResponse(
                promotion.getId(),
                promotion.getSongTitle(),
                promotion.getImageUrl(),
                promotion.getCreatedAt(),
                lastActivityAt,
                trackingLinkClickCount,
                streamingLinkClickCount,
                analysisSummary
        );
    }

    private MyPagePromotionTitleItemResponse toMyPagePromotionTitleItemResponse(MusicPromotion promotion) {
        return new MyPagePromotionTitleItemResponse(
                promotion.getId(),
                promotion.getSongTitle()
        );
    }

    private Comparator<MyPagePromotionItemResponse> myPagePromotionComparator() {
        return Comparator
                .comparing(this::hasUnreadResult)
                .reversed()
                .thenComparing(this::unreadDiagnosedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(this::lastActivityAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(MyPagePromotionItemResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(MyPagePromotionItemResponse::getPromotionId, Comparator.reverseOrder());
    }

    private boolean hasUnreadResult(MyPagePromotionItemResponse response) {
        return response.getAnalysis() != null && response.getAnalysis().isHasUnreadResult();
    }

    private LocalDateTime unreadDiagnosedAt(MyPagePromotionItemResponse response) {
        if (hasUnreadResult(response) && response.getAnalysis() != null) {
            return response.getAnalysis().getDiagnosedAt();
        }
        return null;
    }

    private LocalDateTime lastActivityAt(MyPagePromotionItemResponse response) {
        return response.getLastActivityAt();
    }
}
