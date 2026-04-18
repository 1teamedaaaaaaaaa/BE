package com.hoppin.domain.MusicPromotion.controller;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.service.MusicPromotionService;
import com.hoppin.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music-promotions")
@RequiredArgsConstructor
public class MusicPromotionController {

    private final MusicPromotionService musicPromotionService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMusicPromotionResponse>> createMusicPromotion(
            @RequestHeader("X-Musician-Id") Long musicianId,
            @RequestBody CreateMusicPromotionRequest request
    ) {
        CreateMusicPromotionResponse response = musicPromotionService.createMusicPromotion(musicianId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "음악 홍보가 생성되었습니다."));
    }
}
