package com.codezerotoone.mvp.global.security.token.support;

import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
import com.codezerotoone.mvp.global.security.token.dto.external.OAuth2GrantedToken;
import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("qa.test | prod")
@Slf4j
public class RestTemplateGoogleTokenProcessor implements TokenProcessor {
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_ME_URL = "https://www.googleapis.com/userinfo/v2/me";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public RestTemplateGoogleTokenProcessor(RestTemplate restTemplate,
                                            @Value("${oauth2.google.client-id}") String clientId,
                                            @Value("${oauth2.google.client-secret}")String clientSecret) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public GrantedTokenInfo grantToken(String code, String redirectUri) throws UnsupportedCodeException {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("client_id", this.clientId);
        requestBody.add("client_secret", this.clientSecret);

        RequestEntity<?> requestEntity = RequestEntity.post(GOOGLE_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody);

        ResponseEntity<OAuth2GrantedToken> responseEntity =
                this.restTemplate.exchange(requestEntity, OAuth2GrantedToken.class);
        if (responseEntity.getStatusCode().is4xxClientError()) {
            throw new UnsupportedCodeException("Code is invalid");
        }

        OAuth2GrantedToken responseBody = responseEntity.getBody();

        return GrantedTokenInfo.builder()
                .accessToken(responseBody.getAccessToken())
                .refreshToken(responseBody.getRefreshToken())
                .id(extractIdFromIdToken(responseBody.getIdToken()))
                .authVendor(AuthVendor.GOOGLE)
                .build();
    }

    private String extractIdFromIdToken(String idToken) {
        // 참고: https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#oidc-id-token
        if (log.isDebugEnabled()) {
            log.debug("idToken={}", idToken);
        }
        ResponseEntity<String> response = this.restTemplate.getForEntity("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken,
                String.class);
        return new JSONObject(response.getBody()).getString("sub");
    }

    @Override
    public OAuth2AuthenticationInfo authenticate(String accessToken) throws InvalidAccessTokenException {
        RequestEntity<Void> requestEntity = RequestEntity.get(GOOGLE_ME_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is4xxClientError()) {
            throw new InvalidAccessTokenException("Access Token is not valid");
        }

        JSONObject responseBody = new JSONObject(responseEntity.getBody());

        return OAuth2AuthenticationInfo.builder()
                .id(responseBody.getString("id"))
                .build();
    }

    @Override
    public OAuth2UserInfo retrieveUserInfo(String accessToken) throws InvalidAccessTokenException {
        RequestEntity<Void> requestEntity = RequestEntity.get(GOOGLE_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is4xxClientError()) {
            if (log.isDebugEnabled()) {
                log.debug("status code: {}, body:\n{}", responseEntity.getStatusCode(), responseEntity.getBody());
            }
            throw new InvalidAccessTokenException("Access Token is not valid");
        }

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());

        try {
            return OAuth2UserInfo.builder()
                    .id(jsonObject.getString("id"))
                    .name(jsonObject.getString("name"))
                    .profileImageUrl(jsonObject.getString("picture"))
                    .build();
        } catch (JSONException e) {
            throw new RuntimeException("구글 로그인 서비스에서 줘야 할 걸 안 줌:\n" + jsonObject, e);
        }
    }

    @Override
    public GrantedTokenInfo refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", this.clientId);
        requestBody.add("client_secret", this.clientSecret);

        RequestEntity<?> requestEntity = RequestEntity.post(GOOGLE_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody);

        ResponseEntity<OAuth2GrantedToken> responseEntity =
                this.restTemplate.exchange(requestEntity, OAuth2GrantedToken.class);

        if (responseEntity.getStatusCode().is4xxClientError()) {
            throw new InvalidRefreshTokenException("Refresh token is not valid");
        }

        OAuth2GrantedToken responseBody = responseEntity.getBody();

        return GrantedTokenInfo.builder()
                .accessToken(responseBody.getAccessToken())
                .refreshToken(responseBody.getRefreshToken())
                .id(extractIdFromIdToken(responseBody.getIdToken()))
                .authVendor(AuthVendor.GOOGLE)
                .build();
    }

    @Override
    public AuthVendor getAuthVendor() {
        return AuthVendor.GOOGLE;
    }
}
