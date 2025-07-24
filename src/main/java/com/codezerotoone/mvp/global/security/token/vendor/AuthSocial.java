package com.codezerotoone.mvp.global.security.token.vendor;

import com.codezerotoone.mvp.global.security.token.support.GoogleResponse;
import com.codezerotoone.mvp.global.security.token.support.KakaoResponse;
import com.codezerotoone.mvp.global.security.token.support.OAuth2Response;
import java.util.Arrays;
import java.util.Map;

public enum AuthSocial {
    GOOGLE("google") {
        @Override
        public OAuth2Response createResponse(Map<String, Object> attributes) {
            return new GoogleResponse(attributes);
        }
    },
    KAKAO("kakao") {
        @Override
        public OAuth2Response createResponse(Map<String, Object> attributes) {
            return new KakaoResponse(attributes);
        }
    };

    private final String name;

    AuthSocial(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract OAuth2Response createResponse(Map<String, Object> attributes);

    public static AuthSocial fromName(String name) {
        return Arrays.stream(AuthSocial.values())
                .filter(social -> social.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + name));
    }
}
