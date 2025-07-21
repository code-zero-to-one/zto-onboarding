package com.codezerotoone.mvp.global.security.token.support;

import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;

/**
 * <p>토큰과 관련된 작업을 수행하는 메소드를 정의한 인터페이스.</p>
 * <p>이 인터페이스를 구현한 객체는 다음과 같은 기능을 수행해야 한다.</p>
 * <ol>
 *     <li>Access Token 및 Refresh Token 발급</li>
 *     <li>Access Token 및 Refresh Token 검증</li>
 *     <li>Refresh Token을 사용하여 Access Token 재발급</li>
 *     <li>소셜 로그인 서비스로부터 사용자의 이름, 이메일 등 정보 가져와서 반환</li>
 * </ol>
 *
 * @author PGD
 */
public interface TokenSupport {

    /**
     * Authorization Server로부터 토큰을 발급받는다. Access token은 필수이며, Refresh token은
     * 선택적으로 발급받을 수 있다.
     *
     * @param code 토큰을 발급받기 위한 인가코드
     * @param redirectUri 리다이렉트 URI
     * @param authVendor code를 발급한 인증 서버
     * @return 토큰 정보를 가지고 있는 DTO 객체. 토큰 발행 주체 권한 설정에 따라 <code>refreshToken</code>과
     *         <code>refreshTokenExpiration</code>는 <code>null</code>일 수 있다.
     */
    GrantedTokenInfo grantToken(String code, String redirectUri, AuthVendor authVendor) throws UnsupportedCodeException;

    OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException;

    OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException;

    GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException;
}
