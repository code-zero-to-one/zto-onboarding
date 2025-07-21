package com.codezerotoone.mvp.domain.member.auth.controller;

import com.codezerotoone.mvp.domain.member.auth.controller.schema.RefreshedAccessTokenResponseSchema;
import com.codezerotoone.mvp.domain.member.auth.dto.response.LoginResult;
import com.codezerotoone.mvp.domain.member.auth.dto.response.RefreshedAccessTokenResponseDto;
import com.codezerotoone.mvp.domain.member.auth.service.AuthService;
import com.codezerotoone.mvp.global.api.format.BaseResponse;
import com.codezerotoone.mvp.global.api.schema.LongValueSchema;
import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.support.TokenSupport;
import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
import com.codezerotoone.mvp.global.util.http.cookie.HttpCookieName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 로그인, 토큰 리프레시 등 인증/인가와 관련된 엔드포인트
 *
 * @author PGD
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "인증 API",
        description = "토큰 / 로그인 등 인증/인가와 관련된 API"
)
@Slf4j
public class AuthController {
    private static final long REFRESH_TOKEN_COOKIE_MAX_AGE = 360000;
    private static final Pattern REDIRECTION_DESTINATION_PATTERN =
            Pattern.compile("^(http|https)://[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(:\\d{1,5})?$");

    private final AuthService authService;
    private final TokenSupport tokenSupport;
    private final String serverOrigin;
    private final String clientDomain;
    private final String clientOrigin;

    public AuthController(AuthService authService,
                          TokenSupport tokenSupport,
                          @Value("${server.origin}") String serverOrigin,
                          @Value("${client.domain}") String clientDomain,
                          @Value("${client.origin}") List<String> clientOrigins) {
        this.authService = authService;
        this.tokenSupport = tokenSupport;
        this.serverOrigin = serverOrigin;
        this.clientDomain = clientDomain;
        this.clientOrigin = clientOrigins.getFirst();
    }

    @Operation(
            summary = "OAuth 2.0 소셜 로그인 리다이렉트 URI",
            description = "OAuth 2.0 스펙에 따라 로그인을 진행할 때, 리다이렉트되는 엔드포인트. 프론트에서 이 엔드포인트에 직접 요청할 "
                    + "일은 없고, Auth server (카카오, 구글 등 소셜 로그인 서버)에서 이 엔드포인트로 리다이렉션한다.",
            parameters = @Parameter(
                    name = "authVendor",
                    description = "OAuth 2.0 Auth server",
                    in = ParameterIn.PATH,
                    required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "308",
                            description = "소셜 로그인 성공. Access Token, Refresh Token, 회원 이름, 프로필 사진 URL 반환",
                            headers = {
                                    @Header(name = HttpHeaders.SET_COOKIE,
                                            description = "OAuth 2.0 가이드 문서 참조"),
                                    @Header(name = HttpHeaders.LOCATION,
                                            description = """
                                                    소셜 로그인 후 리다이렉션할 페이지 URL.
                                                    [[[ 가입된 회원일 경우: {프론트엔드 도메인}/ ]]],
                                                    [[[ 가입되지 않은 사용자일 경우: {프론트엔드 도메인}/sign-up ]]],
                                                    [[[ 소셜 로그인이 실패할 경우: {프론트엔드 도메인}/login]]]
                                                    """)
                            }
                    )
            }
    )
    @GetMapping("/{authVendor}/redirect-uri")
    public ResponseEntity<BaseResponse<GrantedTokenInfo>> oauth2Login(@PathVariable("authVendor") String authVendor,
                                                                      @RequestParam("code") String code,
                                                                      // 현재는 state가 Client URL뿐
                                                                      @RequestParam(value = "state", required = false) String state,
                                                                      HttpServletRequest request)
            throws UnsupportedCodeException {

        String redirectUri = this.serverOrigin + request.getRequestURI();
        log.info("Redirect URI: {}", redirectUri);
        log.debug("code: {}", code);
        log.debug("Auth vendor: {}", authVendor);
        if (state != null) {
            log.debug("state={}", state);
        }

        LoginResult loginResult =
                this.authService.loginByOAuth2(code, redirectUri, AuthVendor.valueOfIgnoreCase(authVendor));

        log.debug("loginResult={}", loginResult);

        String redirectionDestination = getRedirectionDestination(state);
        log.debug("redirectionDestination={}", redirectionDestination);

        ResponseCookie refreshTokenCookie = generateCookie(HttpCookieName.REFRESH_TOKEN.getCookieName(),
                loginResult.getRefreshToken(),
                this.clientDomain,
                true);

        log.debug("refreshTokenCookie={}", refreshTokenCookie);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(redirectionDestination)
                .path("/redirection")
                .queryParam("type", "oauth2")
                .queryParam("is-success", true)
                .queryParam("access-token", loginResult.getAccessToken())
                .queryParam("is-guest", loginResult.isNewMember())
                .queryParam("auth-vendor", authVendor);

        if (loginResult.isNewMember()) {
            // 소셜 로그인 개인정보 동의항목 정보 세팅
            uriComponentsBuilder = addParameterIfNotNull(uriComponentsBuilder, "user-name", loginResult.getUserName());
            uriComponentsBuilder = addParameterIfNotNull(uriComponentsBuilder, "profile-image-url", loginResult.getProfileImageUrl());
        } else {
            uriComponentsBuilder = uriComponentsBuilder.queryParam("member-id", loginResult.getMemberId());
        }

        headers.add(
                HttpHeaders.LOCATION,
                uriComponentsBuilder.encode().toUriString()
        );

        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }

    private ResponseCookie generateCookie(String name, String value, String domain, boolean httpOnly) {
        return ResponseCookie.from(name)
                .value(value)
                .domain(this.clientDomain)
                .path("/")
                .httpOnly(httpOnly)
                .secure(true)
                // TODO: Refresh token 유효 시간과 일치시키기
                .maxAge(REFRESH_TOKEN_COOKIE_MAX_AGE)
                .sameSite("None")
                .build();
    }

    private String getRedirectionDestination(String client) {
        String destination = extractDestination(client);
        if (destination != null) {
            log.debug("From query parameter");
            return destination;
        }

        log.debug("Redirect to default origin");
        return this.clientOrigin;
    }

    private String extractDestination(String requesterUri) {
        if (!StringUtils.hasText(requesterUri)) {
            return null;
        }

        URI uri = URI.create(requesterUri);

        if (uri.getAuthority() == null) {
            return null;
        }

        String redirectionTarget = uri.getScheme() + "://" + uri.getAuthority();

        Matcher matcher = REDIRECTION_DESTINATION_PATTERN.matcher(redirectionTarget);
        return matcher.matches()
                ? redirectionTarget
                : null;
    }

    private UriComponentsBuilder addParameterIfNotNull(UriComponentsBuilder builder, String name, String value) {
        if (value != null) {
            return builder.queryParam(name, value);
        }
        return builder;
    }

    @Operation(
            summary = "토큰 리프레시",
            description = "Refresh token으로 새 Access token을 발급받는 엔드포인트.",
            parameters = @Parameter(
                    name = "refresh_token",
                    in = ParameterIn.COOKIE,
                    required = true,
                    description = "Refresh Token. Refresh Token은 기본적으로 HTTP-only 쿠키에 담겨 있다."
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = RefreshedAccessTokenResponseSchema.class),
                                    examples = @ExampleObject("""
                                            {
                                                "statusCode": 201,
                                                "content": {
                                                    "accessToken": "f8310f8asohvh80scvh0zio3hr31d"
                                                }
                                            }
                                            """)
                            ),
                            headers = @Header(
                                    name = HttpHeaders.SET_COOKIE,
                                    description = "Refresh Token; HTTP-only"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    examples = @ExampleObject("""
                                            {
                                                "statusCode": 400,
                                                "timestamp": "2025-06-30T20:46:00.451254",
                                                "errorCode": "AUTH004",
                                                "errorName": "INVALID_REFRESH_TOKEN",
                                                "message": "지원하지 않는 리프레시 토큰입니다."
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/access-token/refresh")
    public ResponseEntity<BaseResponse<RefreshedAccessTokenResponseDto>> accessToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidRefreshTokenException("refresh_token is null");
        }

        GrantedTokenInfo grantedTokenInfo = this.tokenSupport.refreshToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        if (grantedTokenInfo != null && grantedTokenInfo.refreshToken() != null) {
            ResponseCookie refreshTokenCookie = generateCookie(HttpCookieName.REFRESH_TOKEN.getCookieName(),
                    grantedTokenInfo.refreshToken(),
                    this.clientDomain,
                    true);

            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }

        return new ResponseEntity<>(
                BaseResponse.of(new RefreshedAccessTokenResponseDto(grantedTokenInfo.accessToken()), HttpStatus.CREATED),
                headers,
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Who am I?",
            description = "Access token으로부터 사용자 정보를 가져와 반환. memberId만 반환한다.",
            parameters = @Parameter(
                    in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "Authorization 헤더에 Bearer Token을 담아서 전송"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = LongValueSchema.class),
                                    examples = @ExampleObject("""
                                            {
                                                "statusCode": 200,
                                                "content": 10000
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<Long>> whoAmI(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Long memberId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(BaseResponse.of(memberId, HttpStatus.OK));
    }

    @Operation(
            summary = "로그아웃",
            description = "Cookie에 저장된 Refresh token을 제거함으로써 로그아웃 진행. 프론트에서 Access token을 제거할 필요가 "
                    + "있음"
    )
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, invalidateCookie(HttpCookieName.MEMBER_ID.getCookieName(), false).toString());
        headers.add(HttpHeaders.SET_COOKIE, invalidateCookie(HttpCookieName.ACCESS_TOKEN.getCookieName(), false).toString());
        headers.add(HttpHeaders.SET_COOKIE, invalidateCookie(HttpCookieName.REFRESH_TOKEN.getCookieName(), false).toString());
        return new ResponseEntity<>(BaseResponse.of(HttpStatus.OK), headers, HttpStatus.OK);
    }

    private ResponseCookie invalidateCookie(String name, boolean httpOnly) {
        return ResponseCookie.from(name)
                .domain(this.clientDomain)
                .path("/")
                .httpOnly(httpOnly)
                .secure(true)
                .maxAge(0)
                .sameSite("None")
                .build();
    }
}
