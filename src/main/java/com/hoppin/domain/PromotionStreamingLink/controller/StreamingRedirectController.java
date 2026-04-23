package com.hoppin.domain.PromotionStreamingLink.controller;

import com.hoppin.domain.PromotionStreamingLink.service.StreamingRedirectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StreamingRedirectController {

    private final StreamingRedirectService streamingRedirectService;

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
