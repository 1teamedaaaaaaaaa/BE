package com.hoppin.domain.PromotionTrackingLink.controller;

import com.hoppin.domain.PromotionTrackingLink.service.PromotionRedirectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PromotionRedirectController {

    private final PromotionRedirectService promotionRedirectService;

    @GetMapping("/r/{trackingCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String trackingCode,
            HttpServletRequest request
    ) {
        String targetUrl = promotionRedirectService.recordClickAndGetTargetUrl(trackingCode, request);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, targetUrl)
                .build();
    }
}
