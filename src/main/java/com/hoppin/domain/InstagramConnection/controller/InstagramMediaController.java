package com.hoppin.domain.InstagramConnection.controller;

import com.hoppin.domain.InstagramConnection.dto.InstagramMediaResponse;
import com.hoppin.domain.InstagramConnection.service.InstagramMediaService;
import com.hoppin.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstagramMediaController {

    private final InstagramMediaService instagramMediaService;

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
