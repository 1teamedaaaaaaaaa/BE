package com.hoppin.domain.MusicPromotion.controller;

import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionRequest;
import com.hoppin.domain.MusicPromotion.dto.CreateMusicPromotionResponse;
import com.hoppin.domain.MusicPromotion.dto.MusicPromotionDetailResponse;
import com.hoppin.domain.MusicPromotion.service.MusicPromotionService;
import com.hoppin.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Music Promotion", description = "뮤지션 홍보 생성 및 조회 API")
@RestController
@RequestMapping("/api/music-promotions")
@RequiredArgsConstructor
public class MusicPromotionController {

    private final MusicPromotionService musicPromotionService;

    @Operation(
            summary = "뮤지션 홍보 생성",
            description = "뮤지션이 홍보 내용을 입력하고 생성하면 인스타에 공유해야되는 트랙킹 url을 반환합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CreateMusicPromotionResponse>> createMusicPromotion(
            Authentication authentication,
            @RequestBody CreateMusicPromotionRequest request
    ) {
        Long musicianId = Long.parseLong(authentication.getName());

        CreateMusicPromotionResponse response =
                musicPromotionService.createMusicPromotion(musicianId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "음악 홍보가 생성되었습니다."));
    }

    @Operation(
            summary = "뮤지션 홍보 조회",
            description = "promotionId를 가지고 해당하는 홍보 조회"
    )
    @GetMapping("/{promotionId}")
    public ResponseEntity<ApiResponse<MusicPromotionDetailResponse>> getMusicPromotion(
            @PathVariable Long promotionId
    ) {
        MusicPromotionDetailResponse response = musicPromotionService.getMusicPromotion(promotionId);
        return ResponseEntity.ok(ApiResponse.success(response, "음악 홍보를 조회했습니다."));
    }

    @Operation(
            summary = "뮤지션 홍보 삭제",
            description = """
                  promotionId에 해당하는 홍보를 삭제합니다.
                  삭제 시 해당 홍보와 연결된 스트리밍 링크, 추적 링크, 클릭 로그 등 연관 데이터도 함께 삭제됩니다.
                  본인이 생성한 홍보만 삭제할 수 있습니다.
                  """
    )
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<ApiResponse<Void>> deleteMusicPromotion(
            @RequestHeader("X-Musician-Id") Long musicianId,
            @PathVariable Long promotionId
    ) {
        musicPromotionService.deleteMusicPromotion(musicianId, promotionId);
        return ResponseEntity.ok(ApiResponse.success(null, "음악 홍보가 삭제되었습니다."));
    }
}
