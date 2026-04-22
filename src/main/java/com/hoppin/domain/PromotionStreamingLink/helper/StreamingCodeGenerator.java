package com.hoppin.domain.PromotionStreamingLink.helper;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class StreamingCodeGenerator {

    private static final char[] CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private static final int CODE_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);
        }

        return code.toString();
    }
}
