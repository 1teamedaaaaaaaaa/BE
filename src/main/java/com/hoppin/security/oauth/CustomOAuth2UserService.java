package com.hoppin.security.oauth;

import com.hoppin.domain.musician.entity.Musician;
import com.hoppin.domain.musician.entity.MusicianSocialAccount;
import com.hoppin.domain.musician.enumtype.AuthProvider;
import com.hoppin.domain.musician.repository.MusicianRepository;
import com.hoppin.domain.musician.repository.MusicianSocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MusicianRepository musicianRepository;
    private final MusicianSocialAccountRepository musicianSocialAccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = getProvider(registrationId);
        OAuth2UserInfo userInfo = getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        String providerUserId = userInfo.getProviderId();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new OAuth2AuthenticationException("소셜 사용자 식별값이 없습니다.");
        }

        Musician musician = musicianSocialAccountRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(MusicianSocialAccount::getMusician)
                .map(existingMusician -> {
                    validateActive(existingMusician);

                    String resolvedName = resolveName(userInfo.getName(), existingMusician.getName());
                    String resolvedEmail = resolveEmail(userInfo.getEmail(), existingMusician.getEmail());
                    existingMusician.updateProfile(resolvedName, resolvedEmail);

                    return existingMusician;
                })
                .orElseGet(() -> createNewMusicianWithSocialAccount(provider, userInfo, providerUserId));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        Map<String, Object> attributes = Map.of(
                "musicianId", musician.getId(),
                "email", musician.getEmail() == null ? "" : musician.getEmail(),
                "name", musician.getName(),
                "role", "USER"
        );

        return new DefaultOAuth2User(authorities, attributes, "musicianId");
    }

    private Musician createNewMusicianWithSocialAccount(
            AuthProvider provider,
            OAuth2UserInfo userInfo,
            String providerUserId
    ) {
        String name = resolveName(userInfo.getName(), "뮤지션");
        String email = resolveEmail(userInfo.getEmail(), providerUserId + "@social.local");

        Musician musician = new Musician(
                name,
                email,
                null,
                false
        );
        musician = musicianRepository.save(musician);

        MusicianSocialAccount socialAccount = new MusicianSocialAccount(
                musician,
                provider,
                providerUserId,
                userInfo.getEmail(),
                userInfo.getName()
        );

        musicianSocialAccountRepository.save(socialAccount);

        return musician;
    }

    private void validateActive(Musician musician) {
        if (musician.isWithdrawn()) {
            throw new OAuth2AuthenticationException("탈퇴한 회원입니다.");
        }
    }

    private AuthProvider getProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "naver" -> AuthProvider.NAVER;
            case "kakao" -> AuthProvider.KAKAO;
            case "google" -> AuthProvider.GOOGLE;
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인입니다.");
        };
    }

    private OAuth2UserInfo getOAuth2UserInfo(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인입니다.");
        };
    }

    private String resolveName(String socialName, String fallback) {
        if (socialName != null && !socialName.isBlank()) {
            return socialName;
        }
        return fallback;
    }

    private String resolveEmail(String socialEmail, String fallback) {
        if (socialEmail != null && !socialEmail.isBlank()) {
            return socialEmail;
        }
        return fallback;
    }
}