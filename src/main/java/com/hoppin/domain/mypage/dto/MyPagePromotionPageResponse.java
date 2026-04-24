package com.hoppin.domain.mypage.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPagePromotionPageResponse {

    private List<MyPagePromotionItemResponse> promotions;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}