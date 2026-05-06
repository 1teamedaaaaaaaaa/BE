package com.hoppin.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPagePromotionItemResponse {

    private Long promotionId;
    private String title;
    private String coverImageUrl;
    private Long linkClickCount;     // 링크 클릭수
}