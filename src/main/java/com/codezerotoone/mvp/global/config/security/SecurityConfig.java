package com.codezerotoone.mvp.global.config.security;

import com.codezerotoone.mvp.domain.member.auth.service.CustomAccessDeniedHandler;
import com.codezerotoone.mvp.domain.member.auth.service.CustomAuthenticationEntryPoint;
import com.codezerotoone.mvp.domain.member.auth.service.CustomOAuth2UserService;
import com.codezerotoone.mvp.domain.member.auth.service.CustomSuccessHandler;
import com.codezerotoone.mvp.global.security.filter.JwtFilter;
import com.codezerotoone.mvp.global.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 방식 disable
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/custom/login")
                        .userInfoEndpoint(
                                (userInfoEndpointConfig) -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 실패 시 동작
                        .accessDeniedHandler(new CustomAccessDeniedHandler())) //권한 부족 시 동작
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/members")
                        .hasRole("GUEST")
                        .requestMatchers("/api/v1/**")
                        .hasRole("MEMBER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/members/{memberId}/status")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/members/")
                        .hasRole("ADMIN")
                        .requestMatchers("/", "/custom/login", "/login", "/oauth2/authorization/**", "/api/v1/auth/refresh", "/api/auth/check/login")
                        .permitAll()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterAfter(new JwtFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of("http://localhost:3000", "http://localhost:8080")); // 허용할 Origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더
        configuration.setExposedHeaders(List.of("*")); // 노출할 헤더
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}
