package com.hoppin.domain.PromotionTrackingLink.controller;

import com.hoppin.domain.PromotionTrackingLink.service.PromotionRedirectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Tracking Link", description = "스마트 링크 클릭 집계 및 사이트 Redirect API")
@RestController
@RequiredArgsConstructor
public class PromotionRedirectController {

    private final PromotionRedirectService promotionRedirectService;

    @Operation(
            summary = "스마트 링크 클릭 집계 및 사이트 Redirect",
            description = "trackingCode와 함께 API 호출시에 스마트링크 클릭 수 집계를 하고 해당 홍보페이지 url을 헤더에 반환하고 응답 코드는 302 found로 반환."
    )
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
