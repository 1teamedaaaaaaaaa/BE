package com.hoppin.domain.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPagePromotionItemResponse {

    private Long promotionId;
    private String title;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    private Long totalTrackingLinkClickCount;
    private Long totalStreamingLinkClickCount;
    private AnalysisSummary analysis;

    @Getter
    @AllArgsConstructor
    public static class AnalysisSummary {
        private String status;
        private String label;
        private boolean hasUnreadResult;
        private LocalDateTime diagnosedAt;
    }
}
