package com.hoppin.auth.controller;

import com.hoppin.domain.member.entity.Member;
import com.hoppin.domain.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeController {

    private final MemberRepository memberRepository;

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