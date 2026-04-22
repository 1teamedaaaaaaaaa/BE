package com.hoppin.infra.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoppin.infra.ai.dto.AnalysisRequestDto;
import com.hoppin.infra.ai.dto.AnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiWebClient implements OpenAiClient {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .build();

    @Override
    public AnalysisResponseDto call(AnalysisRequestDto request) {
        try {
            String prompt = """
                    너는 독립 뮤지션의 발매 홍보 병목을 진단하는 분석 AI다.

                    사용자가 제공한 JSON 데이터를 바탕으로:
                    1) 가장 막힌 구간 1개를 진단하고
                    2) 그 이유를 한 줄로 설명하고
                    3) 다음 7일 동안 실행할 액션 카드 3개를 제안하라.

                    중요 규칙:
                    - 반드시 JSON만 반환한다.
                    - 설명문, 마크다운, 코드블록을 절대 포함하지 않는다.
                    - 한국어로 작성한다.
                    - 과장하지 말고 입력값 기반으로 보수적으로 해석한다.
                    - 병목은 반드시 1개만 선택한다.
                    - 액션 카드는 정확히 3개를 반환한다.
                    - 각 액션 카드는 실행 가능하고 구체적이어야 한다.
                    - 링크 클릭만 자동 추적, 공유/프로필 방문은 수동 입력 가능하다는 점을 고려한다.
                    - 핵심은 완벽한 데이터가 아니라 판단 가능성이다.

                    병목 해석 기준:
                    - shareCount가 0에 매우 가깝거나 매우 낮으면: 콘텐츠 확산력이 약함
                    - shareCount > profileVisitCount 이면: 더 알아볼 이유가 약함
                    - profileVisitCount > linkClickCount 이면: 스트리밍 CTA가 약함
                    - 전체 수치가 모두 매우 낮으면: 전체 반응이 낮음
                    - 사용자의 mainPainPoint, mainResourceConstraint도 함께 반영하라

                    반환 형식:
                    {
                      "headline": "지금 가장 막힌 지점은 [프로필 방문 → 링크 클릭] 구간입니다",
                      "diagnosis": {
                        "bottleneckType": "LOW_SHARE | LOW_PROFILE_VISIT | LOW_LINK_CLICK | OVERALL_LOW_RESPONSE",
                        "highlightSection": "게시글 공유 → 프로필 방문 | 프로필 방문 → 링크 클릭 | 전체 반응",
                        "shareCount": 0,
                        "profileVisitCount": 0,
                        "linkClickCount": 0,
                        "interpretation": "해석 한 줄"
                      },
                      "actions": [
                        {
                          "title": "액션 제목",
                          "reason": "왜 이걸 해야 하는지",
                          "metric": "연결 지표 1개",
                          "example": "실행 예시 1개"
                        }
                      ]
                    }
                    """;

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", prompt),
                            Map.of("role", "user", "content", objectMapper.writeValueAsString(request))
                    ),
                    "response_format", Map.of("type", "json_object")
            );

            Map<?, ?> response = webClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            Object content = message.get("content");

            if (content == null) {
                throw new RuntimeException("OpenAI 응답 content 없음");
            }

            return objectMapper.readValue(content.toString(), AnalysisResponseDto.class);
        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "OpenAI 호출 실패: " + e.getStatusCode() + " / " + e.getResponseBodyAsString(),
                    e
            );
        } catch (Exception e) {
            throw new RuntimeException("AI 분석 실패", e);
        }
    }
}