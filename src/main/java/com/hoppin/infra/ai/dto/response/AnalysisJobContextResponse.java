package com.hoppin.infra.ai.dto.response;

import java.time.LocalDate;

public record AnalysisJobContextResponse(
        Long analysisJobId,
        Long promotionId,
        Long musicianId,
        String instagramUsername,
        LocalDate sinceDate,
        String mainPainPoint,
        String mainResourceConstraint,
        String promoLink,
        LocalDate releaseDate
) {
}
