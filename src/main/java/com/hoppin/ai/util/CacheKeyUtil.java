package com.hoppin.ai.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CacheKeyUtil {

    private CacheKeyUtil() {}

    public static String makeKey(Object input, ObjectMapper objectMapper) {
        try {
            ObjectMapper mapper = objectMapper.copy();
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = mapper.writeValueAsString(input);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder("ai:analysis:v1:");
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("캐시 키 생성 실패", e);
        }
    }
}