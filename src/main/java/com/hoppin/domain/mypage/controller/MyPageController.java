package com.hoppin.domain.mypage.controller;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.mypage.dto.MyPagePromotionPageResponse;
import com.hoppin.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(
            summary = "내 프로모션 목록 조회",
            description = "로그인한 뮤지션의 프로모션 목록을 검색어 기준으로 조회하고, 링크 클릭 수 내림차순으로 10개씩 페이징합니다."
    )
    @GetMapping("/promotions")
    public MyPagePromotionPageResponse getMyPromotions(
            Authentication authentication,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        Long musicianId = musician.getId();

        return myPageService.getMyPromotions(
                musicianId,
                keyword,
                page
        );
    }
}