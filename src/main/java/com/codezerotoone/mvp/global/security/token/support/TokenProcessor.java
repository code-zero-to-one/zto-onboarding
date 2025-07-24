//package com.codezerotoone.mvp.global.security.token.support;
//
//import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
//import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
//import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
//import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
//import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
//
//public interface TokenProcessor {
//
//    /**
//     * Authorization Server로부터 토큰을 발급받는다. Access token은 필수이며, Refresh token은
//     * 선택적으로 발급받을 수 있다.
//     *
//     * @param code 토큰을 발급받기 위한 인가코드
//     * @param redirectUri 리다이렉트 URI
//     * @return 토큰 정보를 가지고 있는 DTO 객체. 토큰 발행 주체 권한 설정에 따라 <code>refreshToken</code>과
//     *         <code>refreshTokenExpiration</code>는 <code>null</code>일 수 있다.
//     */
//    GrantedTokenInfo grantToken(String code, String redirectUri) throws UnsupportedCodeException;
//
//    OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException;
//
//    OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException;
//
//    GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException;
//
//    AuthVendor getAuthVendor();
//}
