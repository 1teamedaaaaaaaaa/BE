package com.hoppin.infra.s3.controller;

import com.hoppin.infra.s3.service.S3ImageService;
import com.hoppin.infra.s3.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final S3ImageService s3ImageService;

    @PostMapping("/music-promotion-image")
    public PresignedUrlResponse createMusicPromotionImageUploadUrl(
            @RequestParam String filename
    ) {
        return s3ImageService.createMusicPromotionImageUploadUrl(filename);
    }
}