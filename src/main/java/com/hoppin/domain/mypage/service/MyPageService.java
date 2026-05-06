package com.hoppin.domain.mypage.service;

import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MusicPromotionRepository musicPromotionRepository;

    public MyPagePromotionPageResponse getMyPromotions(
            Long musicianId,
            String keyword,
            int page
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by(Sort.Direction.DESC, "linkClickCount")
        );

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
}
