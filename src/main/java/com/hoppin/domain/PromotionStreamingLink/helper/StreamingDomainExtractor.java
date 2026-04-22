package com.hoppin.domain.PromotionStreamingLink.helper;

import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class StreamingDomainExtractor {

    public String extract(String url) {
        URI uri = URI.create(url);

        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("http 또는 https URL만 등록할 수 있습니다.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 스트리밍 URL입니다.");
        }

        return host.toLowerCase();
    }
}
