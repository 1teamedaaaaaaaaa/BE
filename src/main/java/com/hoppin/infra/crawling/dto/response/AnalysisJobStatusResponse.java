package com.hoppin.infra.crawling.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AnalysisJobStatusResponse(
        Long analysisJobId,
        Long promotionId,
        LocalDate sinceDate,
        String instagramUsername,
        String status,
        String mainPainPoint,
        String mainResourceConstraint,
        Integer contentCount,
        Integer followerCount,
        Integer totalLikeCount,
        Integer totalCommentCount,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String errorMessage
) {
}
