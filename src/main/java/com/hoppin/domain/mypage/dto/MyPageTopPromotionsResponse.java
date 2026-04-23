package com.hoppin.domain.mypage.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageTopPromotionsResponse {

    private List<MyPagePromotionItemResponse> promotions;
}