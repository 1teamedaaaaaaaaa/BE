package com.hoppin.domain.musician.controller;

import com.hoppin.domain.musician.service.MusicianWithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Musician", description = "뮤지션 회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MusicianWithdrawController {

    private final MusicianWithdrawService musicianWithdrawService;

    @Operation(
            summary = "회원 탈퇴",
            description = """
                    현재 로그인한 회원을 탈퇴 처리합니다.
                    탈퇴 시 회원 상태는 WITHDRAWN으로 변경되며, withdrawnAt이 저장됩니다.
                    탈퇴 회원 데이터는 즉시 삭제되지 않고 3개월 후 스케줄러에 의해 삭제됩니다.
                    탈퇴 성공 시 인증 토큰 쿠키(accessToken, refreshToken)는 만료 처리됩니다.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal Long musicianId,
            HttpServletResponse response
    ) {
        musicianWithdrawService.withdraw(musicianId);

        expireCookie(response, "accessToken");
        expireCookie(response, "refreshToken");

        return ResponseEntity.noContent().build();
    }

    private void expireCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}