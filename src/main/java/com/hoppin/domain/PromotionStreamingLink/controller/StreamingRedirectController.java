package com.hoppin.domain.PromotionStreamingLink.controller;

import com.hoppin.domain.PromotionStreamingLink.service.StreamingRedirectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Streaming", description = "Streaming 링크 클릭 집계 및 사이트 Redirect API")
@RestController
@RequiredArgsConstructor
public class StreamingRedirectController {

    private final StreamingRedirectService streamingRedirectService;

    @Operation(
            summary = "Streaming 링크 클릭 집계 및 사이트 Redirect",
            description = "스트리밍 code와 함께 API 호출시에 스트리밍 클릭 수 집계를 하고 해당 음원 사이트 url을 헤더에 반환하고 응답 코드는 302 found로 반환."
    )
    @GetMapping("/s/{streamingCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String streamingCode,
            @RequestParam(required = false) String visitId,
            HttpServletRequest request
    ) {
        String destinationUrl = streamingRedirectService.redirect(
                streamingCode,
                visitId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, destinationUrl)
                .build();
    }
}
