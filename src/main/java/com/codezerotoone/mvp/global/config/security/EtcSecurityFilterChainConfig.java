package com.codezerotoone.mvp.global.config.security;

import com.codezerotoone.mvp.global.util.methodoverride.HttpMethodOverrideConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@EnableWebSecurity
public class EtcSecurityFilterChainConfig {

    @Value("${client.cors.allowed-origins}")
    private List<String> clientOrigins;

    @Bean
    @Order
    public SecurityFilterChain etcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors((configurer) -> configurer.configurationSource(corsConfigurationSource()))
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) ->
//                        registry.requestMatchers("/actuator/**", "/error", "/oauth-test.html").permitAll()) // TODO: actuator 필터 설정
                        registry.anyRequest().permitAll()) // TODO: is this ok?
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(this.clientOrigins);
        corsConfiguration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.HEAD.name()
        ));
        corsConfiguration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.COOKIE,
                HttpHeaders.UPGRADE,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpMethodOverrideConstant.HEADER_NAME
        ));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of(
                HttpHeaders.UPGRADE,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.SET_COOKIE
        )); // TODO: specify response headers
        corsConfiguration.setMaxAge(Duration.of(1L, ChronoUnit.HOURS));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
