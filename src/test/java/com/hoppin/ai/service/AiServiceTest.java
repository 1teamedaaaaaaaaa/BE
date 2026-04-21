package com.hoppin.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;
import com.hoppin.infra.ai.service.AiService;
import com.hoppin.infra.ai.service.OpenAiClient;
import com.hoppin.infra.ai.util.CacheKeyUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private OpenAiClient openAiClient;

    private ObjectMapper objectMapper;
    private AiService aiService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aiService = new AiService(redisTemplate, objectMapper, openAiClient);
    }

    @Test
    void 캐시가_없으면_OpenAI를_호출하고_결과를_Redis에_저장한다() throws Exception {
        AnalysisRequestDto request = createRequest();
        AnalysisResponseDto response = createResponse();

        String cacheKey = CacheKeyUtil.makeKey(request, objectMapper);
        String responseJson = objectMapper.writeValueAsString(response);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(openAiClient.call(request)).thenReturn(response);

        AnalysisResponseDto result = aiService.callAi(request);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(response);

        verify(valueOperations).get(cacheKey);
        verify(openAiClient, times(1)).call(request);
        verify(valueOperations).set(eq(cacheKey), eq(responseJson), eq(Duration.ofHours(12)));
    }

    @Test
    void 캐시가_있으면_OpenAI를_호출하지_않고_캐시값을_반환한다() throws Exception {
        AnalysisRequestDto request = createRequest();
        AnalysisResponseDto cachedResponse = createResponse();

        String cacheKey = CacheKeyUtil.makeKey(request, objectMapper);
        String cachedJson = objectMapper.writeValueAsString(cachedResponse);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(cachedJson);

        AnalysisResponseDto result = aiService.callAi(request);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(cachedResponse);

        verify(valueOperations).get(cacheKey);
        verify(openAiClient, never()).call(any());
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void 캐시_JSON이_깨져있으면_삭제후_OpenAI를_다시_호출한다() throws Exception {
        AnalysisRequestDto request = createRequest();
        AnalysisResponseDto response = createResponse();

        String cacheKey = CacheKeyUtil.makeKey(request, objectMapper);
        String brokenJson = "{invalid-json";
        String responseJson = objectMapper.writeValueAsString(response);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(brokenJson);
        when(openAiClient.call(request)).thenReturn(response);

        AnalysisResponseDto result = aiService.callAi(request);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(response);

        verify(redisTemplate).delete(cacheKey);
        verify(openAiClient, times(1)).call(request);
        verify(valueOperations).set(eq(cacheKey), eq(responseJson), eq(Duration.ofHours(12)));
    }

    private AnalysisRequestDto createRequest() {
        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setShareCount(10);
        request.setProfileVisitCount(30);
        request.setLinkClickCount(5);
        request.setMainPainPoint("링크 클릭이 적다");
        request.setMainResourceConstraint("시간");
        request.setContentCountIn28Days(5);
        request.setPeriodLabel("2주");
        return request;
    }

    private AnalysisResponseDto createResponse() {
        AnalysisResponseDto response = new AnalysisResponseDto();
        response.setHeadline("지금 가장 막힌 지점은 프로필 방문 → 링크 클릭 구간입니다");
        return response;
    }
}