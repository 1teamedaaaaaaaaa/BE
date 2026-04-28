package com.hoppin.domain.Instagram.service;

import com.hoppin.domain.Instagram.client.InstagramOAuthClient;
import com.hoppin.domain.Instagram.dto.InstagramLongLivedTokenResponse;
import com.hoppin.domain.Instagram.dto.InstagramMeResponse;
import com.hoppin.domain.Instagram.dto.InstagramOAuthResponse;
import com.hoppin.domain.Instagram.dto.InstagramTokenResponse;
import com.hoppin.domain.InstagramConnection.entity.InstagramConnection;
import com.hoppin.domain.InstagramConnection.repository.InstagramConnectionRepository;
import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.repository.MusicianRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstagramOAuthService {

    private final InstagramOAuthClient instagramOAuthClient;
    private final InstagramConnectionRepository instagramConnectionRepository;
    private final MusicianRepository musicianRepository;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Transactional
    public InstagramOAuthResponse connect(Long musicianId, String code) {
        Musician musician = musicianRepository.findById(musicianId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 뮤지션입니다."));

        InstagramTokenResponse shortLivedToken =
                instagramOAuthClient.requestShortLivedToken(code);

        InstagramLongLivedTokenResponse longLivedToken =
                instagramOAuthClient.requestLongLivedToken(shortLivedToken.accessToken());

        InstagramMeResponse me =
                instagramOAuthClient.requestMe(longLivedToken.accessToken());

        LocalDateTime tokenExpiresAt = LocalDateTime.now()
                .plusSeconds(longLivedToken.expiresIn());

        String instagramAccountId = resolveInstagramAccountId(me, shortLivedToken);

        InstagramConnection connection = instagramConnectionRepository
                .findByMusicianId(musicianId)
                .map(existingConnection -> updateExistingConnection(
                        existingConnection,
                        musician,
                        instagramAccountId,
                        me.username(),
                        longLivedToken.accessToken(),
                        tokenExpiresAt
                ))
                .orElseGet(() -> instagramConnectionRepository
                        .findByInstagramAccountId(instagramAccountId)
                        .map(existingConnection -> updateExistingConnection(
                                existingConnection,
                                musician,
                                instagramAccountId,
                                me.username(),
                                longLivedToken.accessToken(),
                                tokenExpiresAt
                        ))
                        .orElseGet(() -> createNewConnection(
                                musician,
                                instagramAccountId,
                                me.username(),
                                longLivedToken.accessToken(),
                                tokenExpiresAt
                        )));

        return new InstagramOAuthResponse(
                connection.getMusician().getId(),
                connection.getInstagramAccountId(),
                connection.getInstagramUsername()
        );
    }

    @Transactional
    public String connectAndGetRedirectUrl(Long musicianId, String code, String state) {
        InstagramOAuthResponse response = connect(musicianId, code);
        String frontendPath = resolveFrontendPath(state);

        return UriComponentsBuilder
                .fromUriString(frontendBaseUrl + frontendPath)
                .queryParam("instagramConnected", true)
                .queryParam("musicianId", response.musicianId())
                .queryParam("instagramAccountId", response.instagramAccountId())
                .queryParam("instagramUsername", response.instagramUsername())
                .queryParam("message", "Instagram 계정 연동이 완료되었습니다.")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }

    public String buildLoginRedirectUrl(HttpServletRequest request, String state) {
        String callbackUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            callbackUrl = callbackUrl + "?" + request.getQueryString();
        }

        return UriComponentsBuilder
                .fromUriString(frontendBaseUrl + "/login")
                .queryParam("message", "로그인이 필요합니다.")
                .queryParam("redirect", resolveFrontendPath(state))
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }

    private String resolveFrontendPath(String state) {
        if (state == null || state.isBlank()) {
            return "/mypage";
        }

        if (!state.startsWith("/") || state.startsWith("//")) {
            return "/mypage";
        }

        return state;
    }

    private String resolveInstagramAccountId(
            InstagramMeResponse me,
            InstagramTokenResponse shortLivedToken
    ) {
        if (me.userId() != null && !me.userId().isBlank()) {
            return me.userId();
        }

        return String.valueOf(shortLivedToken.userId());
    }

    private InstagramConnection updateExistingConnection(
            InstagramConnection connection,
            Musician musician,
            String instagramAccountId,
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        connection.reconnect(musician, instagramAccountId, instagramUsername, accessToken, tokenExpiresAt);
        return connection;
    }

    private InstagramConnection createNewConnection(
            Musician musician,
            String instagramAccountId,
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        return instagramConnectionRepository.save(
                new InstagramConnection(
                        musician,
                        instagramAccountId,
                        instagramUsername,
                        accessToken,
                        tokenExpiresAt
                )
        );
    }
}
