package com.hoppin.domain.Instagram.dto;

import java.util.List;

public record InstagramInsightApiResponse(
        List<InstagramInsightData> data
) {

    public record InstagramInsightData(
            String name,
            String period,
            List<InstagramInsightValue> values
    ) {
    }

    public record InstagramInsightValue(
            Long value
    ) {
    }

    public Long getMetricValue(String metricName) {
        if (data == null || data.isEmpty()) {
            return 0L;
        }

        return data.stream()
                .filter(metric -> metricName.equals(metric.name()))
                .findFirst()
                .flatMap(metric -> metric.values().stream().findFirst())
                .map(InstagramInsightValue::value)
                .orElse(0L);
    }
}
