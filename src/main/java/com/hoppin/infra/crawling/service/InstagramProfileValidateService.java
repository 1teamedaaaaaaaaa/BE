package com.hoppin.infra.crawling.service;

import com.hoppin.infra.crawling.dto.response.InstagramProfileValidateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramProfileValidateService {

    @Value("${crawler.instagram.base-url}")
    private String crawlerBaseUrl;

    public InstagramProfileValidateResponse validate(String instagramUsername) {
        String normalizedUsername = normalizeInstagramUsername(instagramUsername);

        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            return InstagramProfileValidateResponse.builder()
                    .valid(false)
                    .status("INVALID_USERNAME")
                    .message("인스타그램 계정을 입력해 주세요.")
                    .build();
        }

        if (!normalizedUsername.matches("^[A-Za-z0-9._]{1,30}$")) {
            return InstagramProfileValidateResponse.builder()
                    .valid(false)
                    .status("INVALID_USERNAME")
                    .message("올바른 인스타그램 계정 형식이 아닙니다.")
                    .build();
        }

        try {
            String validateUrl = crawlerBaseUrl + "/profile/validate";

            log.info("Instagram validate request url = {}", validateUrl);
            log.info("Instagram validate username = {}", normalizedUsername);

            InstagramProfileValidateResponse response = RestClient.create()
                    .post()
                    .uri(validateUrl)
                    .body(new CrawlerValidateRequest(normalizedUsername))
                    .retrieve()
                    .body(InstagramProfileValidateResponse.class);

            if (response == null) {
                return validationFailed();
            }

            return response;
        } catch (Exception e) {
            log.warn(
                    "인스타그램 프로필 검증 실패. crawlerBaseUrl={}, username={}",
                    crawlerBaseUrl,
                    normalizedUsername,
                    e
            );

            return validationFailed();
        }
    }

    private InstagramProfileValidateResponse validationFailed() {
        return InstagramProfileValidateResponse.builder()
                .valid(false)
                .status("VALIDATION_FAILED")
                .message("인스타그램 계정 확인 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                .build();
    }

    private String normalizeInstagramUsername(String value) {
        if (value == null) {
            return null;
        }

        String username = value.trim();

        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        if (username.contains("instagram.com/")) {
            String[] parts = username.split("instagram.com/");
            username = parts.length > 1 ? parts[1] : username;
            username = username.split("[/?#]")[0];
        }

        if (username.endsWith("/")) {
            username = username.substring(0, username.length() - 1);
        }

        return username.trim();
    }

    private record CrawlerValidateRequest(
            String instagramUsername
    ) {
    }
}