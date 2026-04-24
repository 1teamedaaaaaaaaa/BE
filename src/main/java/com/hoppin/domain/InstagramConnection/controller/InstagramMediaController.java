package com.hoppin.domain.InstagramConnection.controller;

import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramConnection.service.InstagramMediaService;
import com.hoppin.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Instagram Graph API", description = "외부 API, Instagram Graph API")
@RestController
@RequiredArgsConstructor
public class InstagramMediaController {

    private final InstagramMediaService instagramMediaService;

    // 이 GET api 한번 호출하면 2개의 인스타그램 api를 호출
    // 1. /v22.0/me/media                 -> 해당 계정의 모든 게시물들 조회하는 api
    // 2. /v22.0/" + mediaId + "/insights -> 각각의 게시물들의 공유수, 프로필 방문수, 도달수를 따로 한번더 호출

    @Operation(
            summary = "뮤지션의 모든 게시물 상세 내용들과 인사이트 지표 조회",
            description = "뮤지션 ID로 조회하면, 해당 인스타 계정에서 모든 게시물의 상세 내용들과 인사이트 지표를 조회합니다."
    )
    @GetMapping("/api/musicians/me/instagram/media")
    public ResponseEntity<ApiResponse<List<InstagramMediaResponse>>> getInstagramMedia(
            @RequestHeader("X-Musician-Id") Long musicianId
    ) {
        List<InstagramMediaResponse> response =
                instagramMediaService.getMediaList(musicianId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Instagram 게시물을 조회했습니다.")
        );
    }
}
