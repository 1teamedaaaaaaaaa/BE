package com.hoppin.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("=== OAuth2 로그인 실패 ===");
        log.error("exception class = {}", exception.getClass().getName());
        log.error("exception message = {}", exception.getMessage(), exception);

        String redirectUrl = frontendBaseUrl + "/login?error=login_failed";

        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            String errorCode = oauth2Exception.getError().getErrorCode();
            String errorDescription = oauth2Exception.getError().getDescription();

            log.error("oauth2 error code = {}", errorCode);
            log.error("oauth2 error description = {}", errorDescription);

            if ("withdrawn".equals(errorCode)) {
                String withdrawnAt = errorDescription == null ? "" : errorDescription;

                redirectUrl = frontendBaseUrl
                        + "/login?error=withdrawn"
                        + "&withdrawnAt="
                        + URLEncoder.encode(withdrawnAt, StandardCharsets.UTF_8);
            }
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}