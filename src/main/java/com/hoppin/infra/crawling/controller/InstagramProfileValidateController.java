package com.hoppin.infra.crawling.controller;

import com.hoppin.infra.crawling.dto.request.InstagramProfileValidateRequest;
import com.hoppin.infra.crawling.dto.response.InstagramProfileValidateResponse;
import com.hoppin.infra.crawling.service.InstagramProfileValidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Instagram Crawling", description = "인스타그램 크롤링 및 프로필 검증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawling/instagram")
public class InstagramProfileValidateController {

    private final InstagramProfileValidateService instagramProfileValidateService;

    @Operation(
            summary = "인스타그램 프로필 사전 검증",
            description = """
                    진단 시작 전에 인스타그램 계정이 분석 가능한 상태인지 확인합니다.

                    이 API는 게시글 전체 크롤링을 수행하지 않고,
                    인스타그램 프로필 페이지만 가볍게 확인합니다.

                    검증 항목:
                    - 인스타그램 username 형식
                    - 계정 존재 여부
                    - 비공개 계정 여부
                    - 로그인 페이지로 이동되는지 여부
                    - 프로필 페이지 접근 가능 여부

                    valid가 true인 경우에만 진단 job 생성 API를 호출하면 됩니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/profile/validate")
    public InstagramProfileValidateResponse validateProfile(
            @RequestBody InstagramProfileValidateRequest request
    ) {
        return instagramProfileValidateService.validate(request.getInstagramUsername());
    }
}