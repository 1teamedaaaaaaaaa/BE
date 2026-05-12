package com.hoppin.infra.mail.dto;

public record AnalysisMailInfo(
        String email,
        String musicianName,
        String albumName,
        String reportImageUrl,
        String detailPageUrl
) {
}