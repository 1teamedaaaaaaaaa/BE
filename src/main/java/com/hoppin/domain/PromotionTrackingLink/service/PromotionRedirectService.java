package com.hoppin.domain.PromotionTrackingLink.service;

import com.hoppin.domain.PromotionTrackingClick.entity.PromotionTrackingClick;
import com.hoppin.domain.PromotionTrackingClick.repository.PromotionTrackingClickRepository;
import com.hoppin.domain.PromotionTrackingLink.entity.PromotionTrackingLink;
import com.hoppin.domain.PromotionTrackingLink.repository.PromotionTrackingLinkRepository;
import com.hoppin.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionRedirectService {

    private final PromotionTrackingLinkRepository trackingLinkRepository;
    private final PromotionTrackingClickRepository trackingClickRepository;

    @Transactional
    public String recordClickAndGetTargetUrl(String trackingCode, HttpServletRequest request) {
        PromotionTrackingLink trackingLink = trackingLinkRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("홍보 추적 링크를 찾을 수 없습니다."));

        if (!trackingLink.isActive()) {
            throw new IllegalArgumentException("비활성화된 홍보 추적 링크입니다.");
        }

        String visitId = UUID.randomUUID().toString();

        PromotionTrackingClick click = new PromotionTrackingClick(
                trackingLink,
                visitId,
                trackingLink.getTrackingUrl(),
                extractClientIp(request),
                request.getHeader("User-Agent"),
                request.getHeader("Referer")
        );

        trackingClickRepository.save(click);

        return appendVisitId(trackingLink.getTargetUrl(), visitId);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    private String appendVisitId(String targetUrl, String visitId) {
        String separator = targetUrl.contains("?") ? "&" : "?";
        return targetUrl + separator + "visitId=" + visitId;
    }
}
