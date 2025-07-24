package com.codezerotoone.mvp.global.security.token.support;

public interface OAuth2Response {
    String getProvider(); //제공자 (google, kakao)
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImage();
}
