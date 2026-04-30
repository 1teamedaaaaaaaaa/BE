package com.hoppin.security.controller;

import com.hoppin.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DEV", description = "개발용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dev")
public class DevTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "개발용 JWT 발급",
            description = "개발/로컬 환경에서 특정 musicianId로 테스트용 JWT를 발급합니다."
    )
    @PostMapping("/token")
    public Map<String, String> issueDevToken(@RequestParam Long musicianId) {
        String accessToken = jwtTokenProvider.createAccessToken(musicianId, "USER");

        return Map.of(
                "accessToken", accessToken,
                "tokenType", "Bearer"
        );
    }
}