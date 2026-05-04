package com.hoppin.auth.controller;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianSocialAccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Musician", description = "회원 정보 조회 API")
@RestController
@RequiredArgsConstructor
public class MeController {

    private final MusicianSocialAccountRepository musicianSocialAccountRepository;

    @Operation(
            summary = "내 정보 조회",
            description = "JWT 인증 정보를 기반으로 현재 로그인한 사용자의 회원 정보를 조회합니다."
    )
    @GetMapping("/api/me")
    public MeResponse me(Authentication authentication) {
        Musician musician = (Musician) authentication.getPrincipal();

        String provider = musicianSocialAccountRepository
                .findByMusicianId(musician.getId())
                .map(account -> account.getProvider().name())
                .orElse("");

        return new MeResponse(
                musician.getId(),
                musician.getName(),
                musician.getEmail() == null ? "" : musician.getEmail(),
                provider
        );
    }

    public record MeResponse(
            Long id,
            String artistName,
            String email,
            String provider
    ) {
    }
}