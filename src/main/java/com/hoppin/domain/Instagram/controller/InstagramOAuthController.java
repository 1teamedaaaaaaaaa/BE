package com.hoppin.domain.Instagram.controller;

import com.hoppin.domain.Instagram.service.InstagramOAuthService;
import com.hoppin.domain.musician.entity.Musician;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

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
    public ResponseEntity<Void> callback(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam("code") String code
    ) throws IOException {
        String redirectUrl;

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Musician musician)) {

            redirectUrl = instagramOAuthService.buildLoginRedirectUrl(request, state);
        } else {
            Long musicianId = musician.getId();
            redirectUrl = instagramOAuthService.connectAndGetRedirectUrl(musicianId, code, state);
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
