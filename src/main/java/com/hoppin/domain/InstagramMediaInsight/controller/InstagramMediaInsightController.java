package com.hoppin.domain.InstagramMediaInsight.controller;

import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramMediaInsight.service.InstagramMediaInsightService;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Instagram Media Insight", description = "뮤지션 Instagram 게시물 인사이트 저장 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/musicians/me/instagram/media")
public class InstagramMediaInsightController {

    private final InstagramMediaInsightService instagramMediaInsightService;

    @Operation(
            summary = "Instagram 게시물 인사이트 동기화",
            description = "현재 로그인한 뮤지션의 Instagram 게시물과 인사이트 지표를 조회한 뒤 스냅샷과 상세 지표로 DB에 저장합니다."
    )
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<List<InstagramMediaResponse>>> syncInstagramMedia(
            Authentication authentication
    ) {
        Musician musician = (Musician) authentication.getPrincipal();
        Long musicianId = musician.getId();

        List<InstagramMediaResponse> response =
                instagramMediaInsightService.syncMediaInsights(musicianId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Instagram 게시물 지표를 저장했습니다.")
        );
    }
}