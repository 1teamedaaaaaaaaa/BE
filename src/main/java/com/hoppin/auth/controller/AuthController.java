package com.hoppin.auth.controller;

import com.hoppin.auth.token.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 및 토큰 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "액세스 토큰 재발급",
            description = "HttpOnly 쿠키에 저장된 refreshToken을 검증한 뒤 새로운 accessToken을 발급합니다."
    )
    @PostMapping("/token")
    public ResponseEntity<?> issueAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("message", "유효한 리프레시 토큰이 없습니다."));
        }

        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(memberId, "USER");

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @Operation(
            summary = "로그아웃",
            description = "refreshToken 쿠키를 삭제하여 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 운영 HTTPS 붙이면 true
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    @Operation(
            summary = "소셜 로그인 성공 확인",
            description = "네이버 소셜 로그인 성공 후 프론트 또는 브라우저에서 성공 여부를 확인하기 위한 엔드포인트입니다."
    )
    @GetMapping("/success")
    public ResponseEntity<?> success() {
        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }
}