package com.hoppin.domain.MusicPromotion.infrastructure;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import com.hoppin.infra.crawling.entity.PromotionAnalysisJob;
import com.hoppin.infra.crawling.repository.PromotionAnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
        return musicPromotionJpaRepository
                .findMyPagePromotions(musicianId, keyword, pageable)
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
        if (latestJob != null || latestDiagnosis != null) {
            analysisSummary = new MyPagePromotionItemResponse.AnalysisSummary(
                    latestJob == null ? null : latestJob.getStatus().name(),
                    latestDiagnosis == null ? null : latestDiagnosis.getBottleneckType(),
                    latestDiagnosis != null && latestDiagnosis.isUnread()
            );
        }

        return new MyPagePromotionItemResponse(
                promotion.getId(),
                promotion.getSongTitle(),
                promotion.getImageUrl(),
                promotion.getCreatedAt(),
                trackingLinkClickCount,
                streamingLinkClickCount,
                analysisSummary
        );
    }
}
