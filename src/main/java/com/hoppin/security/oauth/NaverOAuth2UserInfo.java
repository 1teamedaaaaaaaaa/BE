package com.hoppin.security.oauth;

import java.util.Map;

@SuppressWarnings("unchecked")
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        String name = (String) response.get("name");
        return (name == null || name.isBlank()) ? "네이버사용자" : name;
    }
}