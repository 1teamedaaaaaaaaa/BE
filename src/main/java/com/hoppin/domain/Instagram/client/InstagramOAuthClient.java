package com.hoppin.domain.Instagram.client;

import com.hoppin.domain.Instagram.dto.InstagramLongLivedTokenResponse;
import com.hoppin.domain.Instagram.dto.InstagramMeResponse;
import com.hoppin.domain.Instagram.dto.InstagramTokenResponse;
import com.hoppin.global.config.InstagramOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InstagramOAuthClient {

    private final InstagramOAuthProperties instagramOAuthProperties;

    private final RestClient restClient = RestClient.create();

    public InstagramTokenResponse requestShortLivedToken(String code) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", instagramOAuthProperties.clientId());
        form.add("client_secret", instagramOAuthProperties.clientSecret());
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", instagramOAuthProperties.redirectUri());
        form.add("code", code);

        return restClient.post()
                .uri(instagramOAuthProperties.tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(InstagramTokenResponse.class);
    }

    public InstagramLongLivedTokenResponse requestLongLivedToken(String shortLivedAccessToken) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.instagram.com")
                        .path("/access_token")
                        .queryParam("grant_type", "ig_exchange_token")
                        .queryParam("client_secret", instagramOAuthProperties.clientSecret())
                        .queryParam("access_token", shortLivedAccessToken)
                        .build())
                .retrieve()
                .body(InstagramLongLivedTokenResponse.class);
    }

    public InstagramMeResponse requestMe(String accessToken) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.instagram.com")
                        .path("/v22.0/me")
                        .queryParam("fields", "user_id,username,account_type,media_count")
                        .queryParam("access_token", accessToken)
                        .build())
                .retrieve()
                .body(InstagramMeResponse.class);
    }
}
