package com.hoppin.domain.PromotionStreamingLink.service;

import com.hoppin.domain.PromotionStreamingClick.entity.PromotionStreamingClick;
import com.hoppin.domain.PromotionStreamingClick.repository.PromotionStreamingClickRepository;
import com.hoppin.domain.PromotionStreamingLink.entity.PromotionStreamingLink;
import com.hoppin.domain.PromotionStreamingLink.repository.PromotionStreamingLinkRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreamingRedirectService {

    private final PromotionStreamingLinkRepository promotionStreamingLinkRepository;
    private final PromotionStreamingClickRepository promotionStreamingClickRepository;

    @Transactional
    public String redirect(
            String streamingCode,
            String visitId,
            HttpServletRequest request
    ) {
        PromotionStreamingLink streamingLink = promotionStreamingLinkRepository
                .findByStreamingCode(streamingCode)
                .orElseThrow(() -> new ResourceNotFoundException("스트리밍 링크를 찾을 수 없습니다."));

        PromotionStreamingClick streamingClick = new PromotionStreamingClick(
                streamingLink,
                visitId,
                buildClickedUrl(request),
                extractIpAddress(request),
                request.getHeader("User-Agent"),
                request.getHeader("Referer")
        );

        promotionStreamingClickRepository.save(streamingClick);

        return streamingLink.getOriginalUrl();
    }

    private String buildClickedUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURL());

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            url.append("?").append(queryString);
        }

        return url.toString();
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
