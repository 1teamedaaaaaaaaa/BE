package com.hoppin.security.oauth;

import com.hoppin.domain.member.entity.AuthProvider;
import com.hoppin.domain.member.entity.Member;
import com.hoppin.domain.member.entity.Role;
import com.hoppin.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.Map;
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

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"naver".equals(registrationId)) {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인입니다.");
        }

        OAuth2UserInfo userInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());

        String providerId = userInfo.getProviderId();
        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException("네이버 사용자 식별값이 없습니다.");
        }

        Member member = memberRepository.findByProviderAndProviderId(AuthProvider.NAVER, providerId)
                .map(existing -> {
                    existing.updateProfile(userInfo.getEmail(), userInfo.getName());
                    return existing;
                })
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(userInfo.getEmail())
                                .name(userInfo.getName())
                                .provider(AuthProvider.NAVER)
                                .providerId(providerId)
                                .role(Role.USER)
                                .build()
                ));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
        );

        Map<String, Object> attributes = Map.of(
                "memberId", member.getId(),
                "email", member.getEmail() == null ? "" : member.getEmail(),
                "name", member.getName(),
                "role", member.getRole().name()
        );

        return new DefaultOAuth2User(authorities, attributes, "memberId");
    }
}