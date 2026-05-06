package com.hoppin.auth.controller;

import com.hoppin.domain.musician.entity.Musician;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Musician", description = "회원 정보 조회 API")
@RestController
public class MeController {

    @Operation(
            summary = "내 정보 조회",
            description = "JWT 인증 정보를 기반으로 현재 로그인한 사용자의 회원 정보를 조회합니다."
    )
    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        Musician musician = (Musician) authentication.getPrincipal();

        return Map.of(
                "id", musician.getId(),
                "artistName", musician.getName(),
                "email", musician.getEmail() == null ? "" : musician.getEmail()
        );
    }
}