package com.hoppin.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String MUSICIAN_ID_CLAIM = "musicianId";
    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long musicianId, String role) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(musicianId))
                .claim(MUSICIAN_ID_CLAIM, musicianId)
                .claim(ROLE_CLAIM, role)
                .claim(TOKEN_TYPE_CLAIM, "ACCESS")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long musicianId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(musicianId))
                .claim(MUSICIAN_ID_CLAIM, musicianId)
                .claim(TOKEN_TYPE_CLAIM, "REFRESH")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getMusicianId(String token) {
        Claims claims = parseClaims(token);

        Object musicianId = claims.get(MUSICIAN_ID_CLAIM);
        if (musicianId instanceof Integer integerId) {
            return integerId.longValue();
        }
        if (musicianId instanceof Long longId) {
            return longId;
        }

        return Long.parseLong(claims.getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get(ROLE_CLAIM, String.class);
    }

    public String getTokenType(String token) {
        return parseClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}