package com.hoppin.security.oauth;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
}