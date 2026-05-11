package com.hoppin.domain.mypage.service;

import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.analysis.repository.PromotionDiagnosisRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionPageResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionTitleItemResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionTitlePageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MusicPromotionRepository musicPromotionRepository;
    private final PromotionDiagnosisRepository promotionDiagnosisRepository;

    public MyPagePromotionPageResponse getMyPromotions(
            Long musicianId,
            String keyword,
            int page
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<MyPagePromotionItemResponse> result =
                musicPromotionRepository.findMyPagePromotions(
                        musicianId,
                        keyword,
                        pageable
                );

        return new MyPagePromotionPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }

    public MyPagePromotionItemResponse getMyPromotionItem(
            Long musicianId,
            Long promotionId
    ) {
        return musicPromotionRepository.findMyPagePromotion(musicianId, promotionId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션이 존재하지 않거나 접근 권한이 없습니다. id=" + promotionId));
    }

    public MyPagePromotionTitlePageResponse getMyPromotionTitles(Long musicianId, int page) {
        Page<MyPagePromotionTitleItemResponse> result =
                musicPromotionRepository.findMyPagePromotionTitles(musicianId, page);

        return new MyPagePromotionTitlePageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }

    public boolean hasUnreadDiagnoses(Long musicianId) {
        return promotionDiagnosisRepository.existsUnreadDiagnosisByMusicianId(musicianId);
    }
}
