package com.hoppin.infra.s3.dto;

public record PresignedUrlResponse(
        String uploadUrl,
        String imageKey,
        String imageUrl
) {
}