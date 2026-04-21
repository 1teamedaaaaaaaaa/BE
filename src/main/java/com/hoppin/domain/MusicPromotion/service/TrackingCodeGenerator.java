package com.hoppin.domain.MusicPromotion.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TrackingCodeGenerator {

    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        char[] code = new char[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            code[i] = CHARACTERS[random.nextInt(CHARACTERS.length)];
        }
        return new String(code);
    }
}
