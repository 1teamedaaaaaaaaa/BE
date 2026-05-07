package com.hoppin.infra.Instagram.dto;

public record InstagramOAuthResponse(
        Long musicianId,
        String instagramAccountId,
        String instagramUsername
) {
}
