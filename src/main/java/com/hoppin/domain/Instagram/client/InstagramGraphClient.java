package com.hoppin.domain.Instagram.client;

import com.hoppin.domain.Instagram.dto.InstagramInsightApiResponse;
import com.hoppin.domain.Instagram.dto.InstagramMediaListApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InstagramGraphClient {

    private final RestClient restClient = RestClient.create();

    public InstagramMediaListApiResponse getMediaList(String accessToken) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.instagram.com")
                        .path("/v22.0/me/media")
                        .queryParam(
                                "fields",
                                "id,caption,media_type,media_url,permalink,timestamp,thumbnail_url"
                        )
                        .queryParam("access_token", accessToken)
                        .build())
                .retrieve()
                .body(InstagramMediaListApiResponse.class);
    }

    public InstagramInsightApiResponse getMediaInsights(
            String mediaId,
            String accessToken
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("graph.instagram.com")
                        .path("/v22.0/" + mediaId + "/insights")
                        .queryParam("metric", "shares,profile_visits,reach")
                        .queryParam("access_token", accessToken)
                        .build())
                .retrieve()
                .body(InstagramInsightApiResponse.class);
    }
}
