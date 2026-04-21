package com.hoppin.infra.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;
import com.hoppin.infra.ai.util.CacheKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AiService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final OpenAiClient openAiClient;

    public AnalysisResponseDto callAi(AnalysisRequestDto request) {
        String cacheKey = CacheKeyUtil.makeKey(request, objectMapper);

        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, AnalysisResponseDto.class);
            } catch (JsonProcessingException e) {
                redisTemplate.delete(cacheKey);
            }
        }

        AnalysisResponseDto response = openAiClient.call(request);

        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(response),
                    Duration.ofHours(12)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 캐시 저장 실패", e);
        }

        return response;
    }
}