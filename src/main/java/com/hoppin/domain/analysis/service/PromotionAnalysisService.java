package com.hoppin.domain.analysis.service;

import com.hoppin.domain.analysis.enumtype.DiagnosisStatus;
import com.hoppin.domain.analysis.entity.PromotionActionPlan;
import com.hoppin.domain.analysis.entity.PromotionDiagnosis;
import com.hoppin.domain.analysis.entity.PromotionDiagnosisMetric;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.domain.MusicPromotion.entity.MusicPromotion;
import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.infra.ai.dto.ActionCardDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;
import com.hoppin.infra.ai.dto.DiagnosisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionAnalysisService {

    private final PromotionDiagnosisRepository promotionDiagnosisRepository;
    private final MusicPromotionRepository musicPromotionRepository;

    public Long saveAnalysisResult(Long promotionId, AnalysisResponseDto responseDto) {
        MusicPromotion musicPromotion = musicPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않습니다. id=" + promotionId));

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
}