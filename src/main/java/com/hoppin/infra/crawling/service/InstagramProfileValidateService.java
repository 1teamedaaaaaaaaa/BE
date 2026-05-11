package com.hoppin.infra.crawling.service;

import com.hoppin.infra.crawling.dto.response.InstagramProfileValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class InstagramProfileValidateService {

    private final RestClient instagramCrawlerRestClient = RestClient.builder()
            .baseUrl("http://instagram-crawler:3001")
            .build();

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
            InstagramProfileValidateResponse response = instagramCrawlerRestClient.post()
                    .uri("/profile/validate")
                    .body(new CrawlerValidateRequest(normalizedUsername))
                    .retrieve()
                    .body(InstagramProfileValidateResponse.class);

            if (response == null) {
                return validationFailed();
            }

            return response;
        } catch (Exception e) {
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