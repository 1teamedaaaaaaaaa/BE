package com.hoppin.security.handler;

import com.hoppin.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        Object musicianIdObj = oAuth2User.getAttributes().get("musicianId");
        if (musicianIdObj == null) {
            throw new IllegalArgumentException("OAuth2 attributes에 musicianId가 없습니다.");
        }

        Long musicianId = ((Number) musicianIdObj).longValue();
        String role = (String) oAuth2User.getAttributes().getOrDefault("role", "USER");

        System.out.println("성공핸들러 진입");
        System.out.println("musicianId = " + musicianId);
        System.out.println("accessToken created");
        System.out.println("refreshToken created");

        String accessToken = jwtTokenProvider.createAccessToken(musicianId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(musicianId);

        System.out.println("accessToken = " + accessToken);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
//로컬
                .sameSite("Lax")
//운영
//               .sameSite("None")
//               .domain(".musicpeak.site")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".musicpeak.site")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        getRedirectStrategy().sendRedirect(request, response, frontendBaseUrl + "/auth/success");
    }
}