package com.codezerotoone.mvp.global.security.token.support;

import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TokenProcessor에게 역할을 위임하는 TokenSupport.
 * 아마 Spring Security에서 설정 파일을 통해 OAuth 2.0 vendor에 따라 설정하는 방법이 있을 텐데...
 *
 * @see RestTemplateGoogleTokenProcessor
 * @see RestTemplateKakaoTokenProcessor
 */
@Component
@Profile("qa.test | prod")
@Slf4j
public class DelegatingTokenSupport implements TokenSupport {
    private final List<TokenProcessor> processors;
    private final Map<AuthVendor, TokenProcessor> processorByAuthVendor;

    public DelegatingTokenSupport(List<TokenProcessor> processors) {
        this.processors = processors;
        this.processorByAuthVendor = processors.stream()
                .collect(Collectors.toMap(TokenProcessor::getAuthVendor, (p) -> p));
    }

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri, AuthVendor authVendor) throws UnsupportedCodeException {
        TokenProcessor processor = this.processorByAuthVendor.get(authVendor);
        if (processor == null) {
            throw new UnsupportedCodeException("No Auth vendor for: " + authVendor);
        }
        return processor.grantToken(code, redirectUri);
    }

    @Override
    public OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException {
        for (TokenProcessor processor : processors) {
            try {
                OAuth2AuthenticationInfo authentication = processor.authenticate(accessToken);
                log.info("Authenticated by: {}", processor.getAuthVendor());
                return authentication;
            } catch (InvalidAccessTokenException e) {
                log.info("Access token not supported by: {}", processor.getAuthVendor());
            }
        }
        throw new InvalidAccessTokenException("유효하지 않은 토큰");
    }

    @Override
    public OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException {
        for (TokenProcessor processor : processors) {
            try {
                OAuth2UserInfo user = processor.retrieveUserInfo(accessToken);
                log.info("Signed in with: {}", processor.getAuthVendor());
                return user;
            } catch (InvalidAccessTokenException e) {
                log.info("Access token not supported by: {}", processor.getAuthVendor());
            }
        }
        throw new InvalidAccessTokenException("Invalid access token");
    }

    @Override
    public GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        for (TokenProcessor processor : processors) {
            try {
                return processor.refreshToken(refreshToken);
            } catch (InvalidRefreshTokenException e) {
                log.info("Refresh token not supported by: {}", processor.getAuthVendor());
            }
        }
        throw new InvalidRefreshTokenException("Invalid refresh token");
    }
}
