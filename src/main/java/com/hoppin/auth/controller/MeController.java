package com.hoppin.auth.controller;

import com.hoppin.domain.member.entity.Member;
import com.hoppin.domain.member.repository.MemberRepository;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 정보 조회 API")
@RestController
@RequiredArgsConstructor
public class MeController {

    private final MemberRepository memberRepository;

    @Operation(
            summary = "내 정보 조회",
            description = "JWT 인증 정보를 기반으로 현재 로그인한 사용자의 회원 정보를 조회합니다."
    )
    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return Map.of(
                "id", member.getId(),
                "email", member.getEmail(),
                "name", member.getName(),
                "role", member.getRole().name(),
                "provider", member.getProvider().name()
        );
    }
}