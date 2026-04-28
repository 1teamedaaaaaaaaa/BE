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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstagramOAuthService {

    private final InstagramOAuthClient instagramOAuthClient;
    private final InstagramConnectionRepository instagramConnectionRepository;
    private final MusicianRepository musicianRepository;

    @Transactional
    public InstagramOAuthResponse connect(String code) {
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
                .findByInstagramAccountId(instagramAccountId)
                .map(existingConnection -> updateExistingConnection(
                        existingConnection,
                        me.username(),
                        longLivedToken.accessToken(),
                        tokenExpiresAt
                ))
                .orElseGet(() -> createNewConnection(
                        instagramAccountId,
                        me.username(),
                        longLivedToken.accessToken(),
                        tokenExpiresAt
                ));

        return new InstagramOAuthResponse(
                connection.getMusician().getId(),
                connection.getInstagramAccountId(),
                connection.getInstagramUsername()
        );
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
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        connection.updateProfileAndToken(instagramUsername, accessToken, tokenExpiresAt);
        return connection;
    }

    private InstagramConnection createNewConnection(
            String instagramAccountId,
            String instagramUsername,
            String accessToken,
            LocalDateTime tokenExpiresAt
    ) {
        String email = "instagram-" + instagramAccountId + "@instagram.local";
        Musician musician = musicianRepository.findByEmail(email)
                .orElseGet(() -> musicianRepository.save(
                        new Musician(
                                instagramUsername,
                                email,
                                null,
                                false
                        )
                ));

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
