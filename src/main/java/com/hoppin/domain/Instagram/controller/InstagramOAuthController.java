package com.hoppin.domain.Instagram.controller;

import com.hoppin.domain.Instagram.dto.InstagramOAuthResponse;
import com.hoppin.domain.Instagram.service.InstagramOAuthService;
import com.hoppin.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstagramOAuthController {

    private final InstagramOAuthService instagramOAuthService;

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
