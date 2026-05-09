package com.hoppin.domain.analysis.service;

import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.analysis.dto.response.PromotionDiagnosisDetailResponse;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionCalculatedMetrics;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionDiagnosisQueryService {

    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;

    public PromotionDiagnosisDetailResponse getDiagnosisDetail(
            Long musicianId,
            Long promotionId,
            Long diagnosisId
    ) {
        MusicPromotion promotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

        validateOwner(promotion, musicianId);

        PromotionDiagnosis diagnosis = promotionDiagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new IllegalArgumentException("진단 결과가 존재하지 않습니다. diagnosisId=" + diagnosisId));

        if (!diagnosis.getMusicPromotion().getId().equals(promotionId)) {
            throw new IllegalArgumentException("해당 홍보에 속한 진단 결과가 아닙니다.");
        }

        PromotionCalculatedMetrics calculatedMetrics = diagnosis.getCalculatedMetrics();

        PromotionActionPlan action = diagnosis.getActionPlans()
                .stream()
                .min(Comparator.comparing(
                        PromotionActionPlan::getActionOrder,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .orElse(null);

        return PromotionDiagnosisDetailResponse.builder()
                .headline(defaultIfBlank(diagnosis.getHeadline(), "홍보 현황이에요"))
                .periodLabel(resolvePeriodLabel(
                        diagnosis.getSinceDate(),
                        diagnosis.getDiagnosedAt()
                ))
                .summaryMetrics(toSummaryMetrics(calculatedMetrics))
                .diagnosis(PromotionDiagnosisDetailResponse.Diagnosis.builder()
                        .highlightSection(defaultIfBlank(diagnosis.getHighlightSection(), "데이터 부족"))
                        .build())
                .action(toAction(action))
                .build();
    }

    private PromotionDiagnosisDetailResponse.SummaryMetrics toSummaryMetrics(
            PromotionCalculatedMetrics calculatedMetrics
    ) {
        if (calculatedMetrics == null) {
            return PromotionDiagnosisDetailResponse.SummaryMetrics.builder()
                    .followerEngagementRate(0.0)
                    .promoClickRateByEngagement(0.0)
                    .streamingClickRateByPromoClick(0.0)
                    .build();
        }

        return PromotionDiagnosisDetailResponse.SummaryMetrics.builder()
                .followerEngagementRate(toZero(calculatedMetrics.getFollowerEngagementRate()))
                .promoClickRateByEngagement(toZero(calculatedMetrics.getPromoClickRateByEngagement()))
                .streamingClickRateByPromoClick(toZero(calculatedMetrics.getStreamingClickRateByPromoClick()))
                .build();
    }

    private PromotionDiagnosisDetailResponse.Action toAction(PromotionActionPlan action) {
        if (action == null) {
            return PromotionDiagnosisDetailResponse.Action.builder()
                    .title("7일 내 실행할 액션")
                    .metric("7일 뒤 핵심 지표 확인")
                    .details("다음 게시글과 홍보 링크 흐름을 확인해보세요.")
                    .build();
        }

        return PromotionDiagnosisDetailResponse.Action.builder()
                .title(defaultIfBlank(action.getTitle(), "7일 내 실행할 액션"))
                .metric(defaultIfBlank(action.getMetric(), "7일 뒤 핵심 지표 확인"))
                .details(defaultIfBlank(action.getDetails(), "다음 게시글과 홍보 링크 흐름을 확인해보세요."))
                .build();
    }

    private String resolvePeriodLabel(
            LocalDate sinceDate,
            LocalDateTime diagnosedAt
    ) {
        if (sinceDate == null || diagnosedAt == null) {
            return "기간 정보 없음";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

        return sinceDate.format(formatter)
                + " - "
                + diagnosedAt.toLocalDate().format(formatter);
    }

    private void validateOwner(MusicPromotion promotion, Long musicianId) {
        if (!promotion.getMusician().getId().equals(musicianId)) {
            throw new IllegalArgumentException("본인의 프로모션만 조회할 수 있습니다.");
        }
    }

    private double toZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}