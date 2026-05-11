package com.hoppin.domain.mypage.controller;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.mypage.dto.MyPagePromotionPageResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionTitlePageResponse;
import com.hoppin.domain.mypage.service.MyPageService;
import com.hoppin.domain.mypage.service.MyPageSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;

@Tag(name = "MyPage", description = "마이페이지 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final MyPageSseService myPageSseService;

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

    @Operation(
            summary = "내 프로모션 제목 목록 조회",
            description = "로그인한 뮤지션의 프로모션 제목 목록을 생성 최신순으로 5개씩 조회합니다."
    )
    @GetMapping("/promotionsTitle")
    public MyPagePromotionTitlePageResponse getMyPromotionTitles(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        return myPageService.getMyPromotionTitles(musician.getId(), page);
    }

    @Operation(
            summary = "내 unread 진단 존재 여부 조회",
            description = "로그인한 뮤지션의 프로모션 중 읽지 않은 진단 결과가 하나라도 있는지 true/false로 반환합니다."
    )
    @GetMapping("/promotions/unread-exists")
    public boolean hasUnreadDiagnoses(Authentication authentication) {
        Musician musician = (Musician) authentication.getPrincipal();
        return myPageService.hasUnreadDiagnoses(musician.getId());
    }

    @Operation(
            summary = "마이페이지 프로모션 실시간 스트림 구독",
            description = "로그인한 뮤지션의 프로모션 진단 상태 변화를 SSE로 구독합니다."
    )
    @GetMapping(value = "/promotions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribePromotionUpdates(Authentication authentication) {
        Musician musician = (Musician) authentication.getPrincipal();
        SseEmitter emitter = myPageSseService.subscribe(musician.getId());

        return ResponseEntity.ok()
                .header("X-Content-Type-Options", "nosniff")
                .header("X-Accel-Buffering", "no")
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .cacheControl(CacheControl.noCache().mustRevalidate().sMaxAge(0, TimeUnit.SECONDS))
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(emitter);
    }
}
