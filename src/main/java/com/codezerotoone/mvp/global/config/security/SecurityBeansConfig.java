//package com.codezerotoone.mvp.global.config.security;
//
//import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
//import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
//import com.codezerotoone.mvp.global.security.exceptionhandler.DefaultAccessDeniedHandler;
//import com.codezerotoone.mvp.global.security.exceptionhandler.DefaultAuthenticationEntryPoint;
//import com.codezerotoone.mvp.global.security.token.introspector.DefaultOpaqueTokenIntrospector;
//import com.codezerotoone.mvp.global.security.token.support.JsonTokenSupport;
//import com.codezerotoone.mvp.global.security.token.support.TokenSupport;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
//import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
//import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//@Configuration
//public class SecurityBeansConfig {
//
//    @Bean
//    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
//    public AuthenticationEntryPoint defaultAuthenticationEntryPoint(ObjectMapper objectMapper) {
//        return new DefaultAuthenticationEntryPoint(objectMapper);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(AccessDeniedHandler.class)
//    public AccessDeniedHandler defaultAccessDeniedHandler(ObjectMapper objectMapper) {
//        return new DefaultAccessDeniedHandler(objectMapper);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(OpaqueTokenIntrospector.class)
//    public OpaqueTokenIntrospector defaultOpaqueTokenIntrospector(TokenSupport tokenSupport,
//                                                                  MemberRepository memberRepository,
//                                                                  RoleService roleService) {
//        return new DefaultOpaqueTokenIntrospector(tokenSupport, memberRepository, roleService);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(BearerTokenResolver.class)
//    public BearerTokenResolver defaultBearerTokenResolver() {
//        return new DefaultBearerTokenResolver();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(TokenSupport.class)
//    public TokenSupport jsonTokenSupport(ObjectMapper objectMapper) {
//        return new JsonTokenSupport(objectMapper);
//    }
//}
