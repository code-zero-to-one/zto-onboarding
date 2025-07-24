//package com.codezerotoone.mvp.global.config.security;
//
//import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
//import com.codezerotoone.mvp.domain.member.auth.dto.RoleDto;
//import com.codezerotoone.mvp.domain.member.auth.dto.response.AllowedEndpointForRole;
//import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
//import com.codezerotoone.mvp.global.security.filter.AuthenticatedRequestCheckFilter;
//import com.codezerotoone.mvp.global.util.methodoverride.HttpMethodOverrideConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
//import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
//import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.time.Duration;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//
///**
// * A configuration class for REST API endpoints (/api/v1)
// *
// * @author PGD
// */
//@Slf4j
//@EnableWebSecurity
//@Configuration
//public class ApiSecurityFilterChainConfig {
//
//    @Value("${client.cors.allowed-origins}")
//    private List<String> accessControlAllowedOrigins;
//
//    @Autowired
//    private AuthenticationEntryPoint authenticationEntryPoint;
//
//    @Autowired
//    private AccessDeniedHandler accessDeniedHandler;
//
//    @Autowired
//    private OpaqueTokenIntrospector tokenIntrospector;
//
//    @Autowired
//    private BearerTokenResolver bearerTokenResolver;
//
//    @Autowired
//    private RoleService roleService;
//
//    /**
//     * <p>Allow every request for the sake of only development convenience.
//     * <p>This <code>SecurityFilterChain</code> shouldn't be registered in production environment.
//     *
//     * @param http Security configuration
//     * @return SecurityFilterChain instance
//     * @throws Exception exception
//     */
//    @Bean
//    @Profile("no-auth")
//    @Order(Integer.MIN_VALUE)
//    public SecurityFilterChain noAuthFilterChain(HttpSecurity http) throws Exception {
//        return commonSecurityConfig(http)
//                .authorizeHttpRequests((request) -> request.anyRequest().permitAll())
//                .build();
//    }
//
//    /**
//     * <p>Default Security filter chain for /api/v1
//     *
//     * @param http Security configuration
//     * @return SecurityFilterChain instance
//     * @throws Exception exception
//     */
//    @Bean
//    @Profile("!no-auth")
//    @Order(Integer.MIN_VALUE)
//    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
//        return commonSecurityConfig(http)
//                .authorizeHttpRequests((request) -> {
//                    // TODO: 애플리케이션 실행 중 동적으로 권한을 정의하기 위한 수단이 필요함
//                    Map<AuthorizedHttpMethod, List<AllowedEndpointForRole>> allAccessPermission =
//                            this.roleService.getAllAccessPermission();
//
//                    log.info("allAccessPermission={}", allAccessPermission);
//
//                    allAccessPermission.forEach((httpMethod, allowedEndpointForRoles) -> {
//                        for (AllowedEndpointForRole allowedEndpointForRole : allowedEndpointForRoles) {
//                            log.info("httpMethod={}, allowedEndpointForRole={}", httpMethod, allowedEndpointForRole);
//                            request.requestMatchers(HttpMethod.valueOf(httpMethod.name()), allowedEndpointForRole.endpoint())
//                                    .hasAnyRole(allowedEndpointForRole.roles()
//                                            .stream()
//                                            .map(RoleDto::getCode)
//                                            .toArray(String[]::new));
//                        }
//                    });
//
//                    request.anyRequest().permitAll();
//                })
//                .build();
//    }
//
//    private HttpSecurity commonSecurityConfig(HttpSecurity http) throws Exception {
//        AuthenticatedRequestCheckFilter authenticatedRequestFilter =
//                new AuthenticatedRequestCheckFilter(this.roleService, this.authenticationEntryPoint);
//        authenticatedRequestFilter.initFilterBean();
//        return http.securityMatcher("/api/v1/**")
//                .csrf(AbstractHttpConfigurer::disable) // TODO: consider applying CSRF protection
//                .cors((configurer) -> configurer.configurationSource(corsConfigurationSource()))
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .addFilterAfter(authenticatedRequestFilter,
//                        BearerTokenAuthenticationFilter.class)
//                .exceptionHandling((config) ->
//                        config.authenticationEntryPoint(this.authenticationEntryPoint)
//                                .accessDeniedHandler(this.accessDeniedHandler))
//                .sessionManagement((session) ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2ResourceServer((resourceServer) ->
//                        resourceServer
//                                .authenticationEntryPoint(this.authenticationEntryPoint)
//                                .bearerTokenResolver(this.bearerTokenResolver)
//                                .opaqueToken((config) -> config.introspector(this.tokenIntrospector)));
//    }
//
//    private CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(this.accessControlAllowedOrigins);
//        corsConfiguration.setAllowedMethods(List.of(
//                HttpMethod.GET.name(),
//                HttpMethod.POST.name(),
//                HttpMethod.PUT.name(),
//                HttpMethod.DELETE.name(),
//                HttpMethod.PATCH.name(),
//                HttpMethod.HEAD.name()
//        ));
//        corsConfiguration.setAllowedHeaders(List.of(
//                HttpHeaders.AUTHORIZATION,
//                HttpHeaders.COOKIE,
//                HttpHeaders.UPGRADE,
//                HttpHeaders.CONTENT_TYPE,
//                HttpHeaders.ACCEPT,
//                HttpMethodOverrideConstant.HEADER_NAME
//        ));
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setExposedHeaders(List.of(
//                HttpHeaders.UPGRADE,
//                HttpHeaders.CONTENT_TYPE,
//                HttpHeaders.SET_COOKIE
//        )); // TODO: specify response headers
//        corsConfiguration.setMaxAge(Duration.of(1L, ChronoUnit.HOURS));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return source;
//    }
//}
