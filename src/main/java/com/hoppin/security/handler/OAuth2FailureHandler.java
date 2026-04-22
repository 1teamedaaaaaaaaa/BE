package com.hoppin.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("=== OAuth2 로그인 실패 ===");
        log.error("exception class = {}", exception.getClass().getName());
        log.error("exception message = {}", exception.getMessage(), exception);

        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            log.error("oauth2 error code = {}", oauth2Exception.getError().getErrorCode());
            log.error("oauth2 error description = {}", oauth2Exception.getError().getDescription());
        }

        getRedirectStrategy().sendRedirect(request, response, "/login?error");
    }
}