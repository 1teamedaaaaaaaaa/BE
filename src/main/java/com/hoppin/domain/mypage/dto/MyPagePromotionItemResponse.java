package com.hoppin.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPagePromotionItemResponse {

    private Long promotionId;
    private String title;
    private String coverImageUrl;

    private Long shareCount;         // 공유수
    private Long profileVisitCount;  // 프로필 유입수
    private Long linkClickCount;     // 링크 클릭수
}