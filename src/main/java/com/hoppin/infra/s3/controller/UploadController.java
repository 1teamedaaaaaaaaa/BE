package com.hoppin.infra.s3.controller;

import com.hoppin.infra.s3.dto.PresignedUrlResponse;
import com.hoppin.infra.s3.service.S3ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Tag(name = "홍보 만들기 시 S3 이미지 업로드 API")
public class UploadController {

    private final S3ImageService s3ImageService;

    @Operation(
            summary = "음악 홍보 이미지 업로드 URL 발급",
            description = """
                    음악 홍보 생성 시 사용할 이미지 업로드용 Presigned URL을 발급합니다.
                    
                    프론트 사용 흐름:
                    1. 사용자가 홍보 만들기 버튼 클릭
                    2. 이 API로 uploadUrl, imageUrl 발급
                    3. 프론트가 uploadUrl로 S3에 PUT 업로드
                    4. 업로드 성공 후 imageUrl을 홍보 생성 API의 imageUrl 필드에 넣어 전송
                    
                    uploadUrl은 S3 업로드 전용 임시 URL이고,
                    imageUrl은 DB에 저장할 이미지 접근 URL입니다.
                    """
    )
    @PostMapping("/music-promotion-image")
    public PresignedUrlResponse createMusicPromotionImageUploadUrl(
            @Parameter(
                    description = "업로드할 원본 이미지 파일명. 확장자는 jpg, jpeg, png, webp만 허용됩니다.",
                    example = "cover.jpg",
                    required = true
            )
            @RequestParam String filename
    ) {
        return s3ImageService.createMusicPromotionImageUploadUrl(filename);
    }
}