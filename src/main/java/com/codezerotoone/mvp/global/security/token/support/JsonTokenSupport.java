package com.codezerotoone.mvp.global.security.token.support;

import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * JSON 토큰을 처리하는 역할을 지닌 {@code TokenSupport} 구현체.
 * {@link com.codezerotoone.mvp.global.config.security.SecurityBeansConfig}에서 Spring Bean으로 등록된다.
 *
 * @author PGD
 * @see com.codezerotoone.mvp.global.config.security.SecurityBeansConfig
 */
@RequiredArgsConstructor
@Slf4j
public class JsonTokenSupport implements TokenSupport {
    private final ObjectMapper objectMapper;

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri, AuthVendor authVendor) throws UnsupportedCodeException {
        String trimmedCode = code.substring(0, 20);
        String token = URLEncoder.encode(String.format("{ \"id\": \"%s\", \"name\": \"안유진\", \"profileImageUrl\": \"https://dimg.donga.com/wps/NEWS/IMAGE/2020/12/09/104244741.2.jpg\"}", trimmedCode), StandardCharsets.UTF_8);
        return GrantedTokenInfo.builder()
                .accessToken(token)
                .refreshToken(token)
                .id(trimmedCode)
                .authVendor(AuthVendor.NATIVE)
                .build();
    }

    @Override
    public OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException {
        try {
            Map<String, String> result = this.objectMapper.readValue(accessToken, new TypeReference<>() {
            });

            return OAuth2AuthenticationInfo.builder()
                    .id(result.get("id"))
                    .build();
        } catch (JsonProcessingException e) {
            throw new InvalidAccessTokenException(e);
        }
    }

    @Override
    public OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException {
        try {
            return this.objectMapper.readValue(URLDecoder.decode(accessToken, StandardCharsets.UTF_8), OAuth2UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new InvalidAccessTokenException("Invalid Access token", e);
        }
    }

    @Override
    public GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        return GrantedTokenInfo.builder()
                .accessToken(refreshToken)
                .refreshToken(refreshToken)
                .id(authenticate(refreshToken).id())
                .authVendor(AuthVendor.NATIVE)
                .build();
    }
}
