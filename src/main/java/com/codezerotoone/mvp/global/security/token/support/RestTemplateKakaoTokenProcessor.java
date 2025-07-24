//package com.codezerotoone.mvp.global.security.token.support;
//
//import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
//import com.codezerotoone.mvp.global.security.token.dto.external.OAuth2GrantedToken;
//import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
//import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
//import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
//import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Base64;
//
//@Component
//@Profile("qa.test | prod")
//@Slf4j
//public class RestTemplateKakaoTokenProcessor implements TokenProcessor {
//    private static final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
//    private static final String KAKAO_OIDC_USER_INFO_URL = "https://kapi.kakao.com/v1/oidc/userinfo";
//    private static final String KAKAO_ME_URL = "https://kapi.kakao.com/v2/user/me";
//    private static final String TOKEN_REQUEST_HEADER_KEY_GRANT_TYPE = "grant_type";
//    private static final String TOKEN_REQUEST_HEADER_VALUE_GRANT_TYPE = "authorization_code";
//    private static final String TOKEN_REQUEST_HEADER_KEY_CLIENT_ID = "client_id";
//    private static final String TOKEN_REQUEST_HEADER_KEY_CLIENT_SECRET = "client_secret";
//    private static final String TOKEN_REQUEST_HEADER_KEY_REDIRECT_URI = "redirect_uri";
//    private static final String TOKEN_REQUEST_HEADER_KEY_CODE = "code";
//
//    private final RestTemplate restTemplate;
//    private final String kakaoClientId;
//    private final String kakaoClientSecret; // Nullable
//
//    public RestTemplateKakaoTokenProcessor(RestTemplate restTemplate,
//                                           @Value("${oauth2.kakao.client-id}") String kakaoClientId,
//                                           @Value("${oauth2.kakao.client-secret}") String kakaoClientSecret) {
//        this.restTemplate = restTemplate;
//        this.kakaoClientId = kakaoClientId;
//        this.kakaoClientSecret = "empty".equals(kakaoClientSecret) ? null : kakaoClientSecret;
//    }
//
//    @Override
//    public GrantedTokenInfo grantToken(String code, String redirectUri) throws UnsupportedCodeException {
//        log.debug("redirect uri: {}", redirectUri);
//
//        RequestEntity<MultiValueMap<String, String>> requestEntity = buildTokenRequestEntity(code, redirectUri);
//
//        ResponseEntity<OAuth2GrantedToken> responseEntity =
//                this.restTemplate.exchange(requestEntity, OAuth2GrantedToken.class);
//
//        if (responseEntity.getStatusCode().is4xxClientError()) {
//            throw new UnsupportedCodeException("Code is not supported.");
//        }
//
//        OAuth2GrantedToken responseBody = responseEntity.getBody();
//
//        if (log.isDebugEnabled()) {
//            log.debug("Response Body:\n{}", responseBody);
//        }
//
//        return GrantedTokenInfo.builder()
//                .accessToken(responseBody.getAccessToken())
//                .refreshToken(responseBody.getRefreshToken())
//                .id(extractIdFromIdToken(responseBody.getIdToken()))
//                .authVendor(AuthVendor.KAKAO)
//                .build();
//    }
//
//    private RequestEntity<MultiValueMap<String, String>> buildTokenRequestEntity(String code, String redirectUri) {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add(TOKEN_REQUEST_HEADER_KEY_GRANT_TYPE, TOKEN_REQUEST_HEADER_VALUE_GRANT_TYPE);
//        params.add(TOKEN_REQUEST_HEADER_KEY_CLIENT_ID, this.kakaoClientId);
//        if (this.kakaoClientSecret != null) {
//            params.add(TOKEN_REQUEST_HEADER_KEY_CLIENT_SECRET, this.kakaoClientSecret);
//        }
//        params.add(TOKEN_REQUEST_HEADER_KEY_REDIRECT_URI, redirectUri);
//        params.add(TOKEN_REQUEST_HEADER_KEY_CODE, code);
////        params.add("client_secret", this.kakaoClientSecret); // TODO: 카카오 로그인 Client Secret 설정
//
//        return RequestEntity.post(KAKAO_TOKEN_REQUEST_URL)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .body(params);
//    }
//
//    private String extractIdFromIdToken(String idToken) {
//        // 참고: https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#oidc-id-token
//        if (log.isDebugEnabled()) {
//            log.debug("idToken={}", idToken);
//        }
//        String payload = idToken.split("\\.")[1];
//        byte[] decoded = Base64.getDecoder().decode(payload);
//        char[] chars = new char[decoded.length];
//        for (int i = 0; i < chars.length; i++) {
//            chars[i] = (char) decoded[i];
//        }
//        return new JSONObject(String.valueOf(chars)).getString("sub");
//    }
//
//    @Override
//    public OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException {
//        RequestEntity<Void> requestEntity = RequestEntity.get(KAKAO_OIDC_USER_INFO_URL)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .build();
//
//        ResponseEntity<String> responseEntity =
//                this.restTemplate.exchange(requestEntity, String.class);
//
//        if (responseEntity.getStatusCode().is4xxClientError()) {
//            throw new InvalidAccessTokenException("Access token not valid");
//        }
//
//        JSONObject responseBody = new JSONObject(responseEntity.getBody());
//        return OAuth2AuthenticationInfo.builder()
//                .id(responseBody.getString("sub"))
//                .build();
//    }
//
//    @Override
//    public OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException {
//        RequestEntity<Void> requestEntity = RequestEntity.get(KAKAO_ME_URL)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .build();
//
//        ResponseEntity<String> responseEntity =
//                this.restTemplate.exchange(requestEntity, String.class);
//
//        if (responseEntity.getStatusCode().is4xxClientError()) {
//            throw new InvalidAccessTokenException("Access Token is not valid");
//        }
//
//        JSONObject responseBody = new JSONObject(responseEntity.getBody());
//        JSONObject kakaoAccount = responseBody.getJSONObject("kakao_account");
//        return OAuth2UserInfo.builder()
//                .id(String.valueOf(responseBody.getLong("id")))
//                .name(kakaoAccount.isNull("name") ? null : kakaoAccount.getString("name"))
//                .profileImageUrl(
//                        kakaoAccount.isNull("profile") || kakaoAccount.getJSONObject("profile").isNull("rofile_image_url")
//                                ? null
//                                : kakaoAccount.getJSONObject("profile").getString("profile_image_url")
//                )
//                .build();
//    }
//
//    @Override
//    public GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException {
//        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
//        bodyParams.add("grant_type", "refresh_token");
//        bodyParams.add("client_id", this.kakaoClientId);
//        bodyParams.add("refresh_token", refreshToken);
////        bodyParams.add("client_secret", this.kakaoClientSecret); // TODO: 카카오 로그인 Client Secret 설정
//
//        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(KAKAO_TOKEN_REQUEST_URL)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(bodyParams);
//
//        ResponseEntity<OAuth2GrantedToken> responseEntity =
//                this.restTemplate.exchange(requestEntity, OAuth2GrantedToken.class);
//
//        if (responseEntity.getStatusCode().is4xxClientError()) {
//            throw new InvalidRefreshTokenException("Refresh token not valid");
//        }
//
//        OAuth2GrantedToken responseBody = responseEntity.getBody();
//
//        return GrantedTokenInfo.builder()
//                .accessToken(responseBody.getAccessToken())
//                .refreshToken(responseBody.getRefreshToken())
//                .id(extractIdFromIdToken(responseBody.getIdToken()))
//                .authVendor(AuthVendor.KAKAO)
//                .build();
//    }
//
//    @Override
//    public AuthVendor getAuthVendor() {
//        return AuthVendor.KAKAO;
//    }
//}
