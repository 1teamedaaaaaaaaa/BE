package com.hoppin.domain.mypage.service;

import com.hoppin.domain.MusicPromotion.repository.MusicPromotionRepository;
import com.hoppin.domain.mypage.dto.MyPagePromotionItemResponse;
import com.hoppin.domain.mypage.dto.MyPagePromotionPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MyPageServiceTest {

    private final MusicPromotionRepository musicPromotionRepository =
            mock(MusicPromotionRepository.class);

    private final MyPageService myPageService =
            new MyPageService(musicPromotionRepository);

    @Test
    @DisplayName("내 프로모션 목록을 10개씩 링크 클릭 수 내림차순으로 조회한다")
    void getMyPromotions_success() {
        // given
        Long musicianId = 1L;
        String keyword = null;
        int page = 0;

        List<MyPagePromotionItemResponse> contents = List.of(
                new MyPagePromotionItemResponse(1L, "봄 시즌 캠페인", "https://example.com/1.jpg", 9L, 88L, 2L),
                new MyPagePromotionItemResponse(2L, "월간 리스너 캠페인", "https://example.com/2.jpg", 12L, 130L, 4L)
        );

        Page<MyPagePromotionItemResponse> mockPage =
                new PageImpl<>(contents, PageRequest.of(0, 10), 20);

        when(musicPromotionRepository.findMyPagePromotions(
                eq(musicianId),
                eq(keyword),
                any(Pageable.class)
        )).thenReturn(mockPage);

        // when
        MyPagePromotionPageResponse response =
                myPageService.getMyPromotions(musicianId, keyword, page);

        // then
        assertThat(response.getPromotions()).hasSize(2);
        assertThat(response.getPromotions().get(0).getLinkClickCount()).isEqualTo(2L);
        assertThat(response.getPromotions().get(1).getLinkClickCount()).isEqualTo(4L);

        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(20);
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.isHasNext()).isTrue();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(musicPromotionRepository).findMyPagePromotions(
                eq(musicianId),
                eq(keyword),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("linkClickCount").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("검색어가 있으면 검색어와 함께 프로모션 목록을 조회한다")
    void getMyPromotions_withKeyword() {
        // given
        Long musicianId = 1L;
        String keyword = "앨범";
        int page = 1;

        List<MyPagePromotionItemResponse> contents = List.of(
                new MyPagePromotionItemResponse(10L, "앨범 아트 공개", "https://example.com/10.jpg", 29L, 240L, 16L)
        );

        Page<MyPagePromotionItemResponse> mockPage =
                new PageImpl<>(contents, PageRequest.of(1, 10), 11);

        when(musicPromotionRepository.findMyPagePromotions(
                eq(musicianId),
                eq(keyword),
                any(Pageable.class)
        )).thenReturn(mockPage);

        // when
        MyPagePromotionPageResponse response =
                myPageService.getMyPromotions(musicianId, keyword, page);

        // then
        assertThat(response.getPromotions()).hasSize(1);
        assertThat(response.getPromotions().get(0).getTitle()).contains("앨범");
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(10);

        verify(musicPromotionRepository).findMyPagePromotions(
                eq(musicianId),
                eq(keyword),
                any(Pageable.class)
        );
    }
}