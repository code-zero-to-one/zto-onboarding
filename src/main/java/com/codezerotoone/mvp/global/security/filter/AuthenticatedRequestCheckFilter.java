package com.codezerotoone.mvp.global.security.filter;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.constant.PrimaryRole;
import com.codezerotoone.mvp.domain.member.auth.dto.response.AllowedEndpointForRole;
import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

// TODO: 이렇게 하는 것보다 Spring Security에서 설정하는 방법이 있는지 찾아보기
@RequiredArgsConstructor
@Slf4j
public class AuthenticatedRequestCheckFilter extends OncePerRequestFilter {
    private static final GrantedAuthority ANONYMOUS_AUTHORITY = new SimpleGrantedAuthority(PrimaryRole.ROLE_ANONYMOUS.name());

    private final RoleService roleService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final Set<AntPathRequestMatcher> antPathRequestMatchers = new HashSet<>();

    @Override
    public void initFilterBean() throws ServletException {
        this.antPathRequestMatchers.clear();

        Map<AuthorizedHttpMethod, List<AllowedEndpointForRole>> allAccessPermission = this.roleService.getAllAccessPermission();

        // TODO: O(N)
        allAccessPermission.forEach((httpMethod, allowedEndpointForRoles) -> {
            for (AllowedEndpointForRole allowedEndpointForRole : allowedEndpointForRoles) {
                this.antPathRequestMatchers.add(new AntPathRequestMatcher(allowedEndpointForRole.endpoint(), httpMethod.name()));
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            processFilter(request, response, filterChain);
            return;
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            processFilter(request, response, filterChain);
            return;
        }

        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = (OAuth2AuthenticatedPrincipal) principal;

        Collection<? extends GrantedAuthority> authorities = oAuth2AuthenticatedPrincipal.getAuthorities();

        if ((authorities == null || authorities.contains(ANONYMOUS_AUTHORITY))
                && !isInWhiteList(request)) {
            this.authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid access token"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isInWhiteList(request)) {
            filterChain.doFilter(request, response);
        } else {
            this.authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid access token"));
        }
    }

    private boolean isInWhiteList(HttpServletRequest request) {
        for (AntPathRequestMatcher matcher : this.antPathRequestMatchers) {
            if (matcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
