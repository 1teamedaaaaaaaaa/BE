package com.hoppin.domain.Instagram.controller;

import com.hoppin.domain.Instagram.dto.InstagramOAuthResponse;
import com.hoppin.domain.Instagram.service.InstagramOAuthService;
import com.hoppin.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인스타 계정 연동", description = "인스타 계정 연동 관련 API")
@RestController
@RequiredArgsConstructor
public class InstagramOAuthController {

    private final InstagramOAuthService instagramOAuthService;

    @Operation(
            summary = "인스타 게정 연동하고 돌아오는 redirect uri",
            description = "유저가 인스타에서 계정 연동을하고 앱에 다시 돌아올때 유저의 브라우저에서 호출하는 url."
    )
    @GetMapping("/instagram/oauth/callback")
    public ResponseEntity<ApiResponse<InstagramOAuthResponse>> callback(
            @RequestParam("code") String code
    ) {
        InstagramOAuthResponse response = instagramOAuthService.connect(code);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Instagram 로그인이 완료되었습니다.")
        );
    }
}
