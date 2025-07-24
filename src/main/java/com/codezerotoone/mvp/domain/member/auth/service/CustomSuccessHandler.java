package com.codezerotoone.mvp.domain.member.auth.service;

import com.codezerotoone.mvp.domain.member.auth.dto.CustomOAuth2User;
import com.codezerotoone.mvp.global.util.CookieUtil;
import com.codezerotoone.mvp.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Long memberId = customUserDetails.getMemberId();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createAccessToken(memberId, role);
        String refreshToken = jwtUtil.createRefreshToken(memberId, role);

        response.addHeader("Set-Cookie", cookieUtil.createCookie("Refresh-Token", refreshToken));
        response.addHeader("Set-Cookie", cookieUtil.createCookie("Authorization", accessToken));

        log.info("Access Token: {}", accessToken);
        log.info("Refresh Token: {}", refreshToken);

        String redirectUrl = "http://localhost:3000"; //Role에 따라 추가적인 회원가입 페이지로 리다이렉트 가능
        response.sendRedirect(redirectUrl);
    }


}
