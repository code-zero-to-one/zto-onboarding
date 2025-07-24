//package com.codezerotoone.mvp.global.security.token.introspector;
//
//import com.codezerotoone.mvp.domain.member.auth.constant.PrimaryRole;
//import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
//import com.codezerotoone.mvp.domain.member.member.entity.Member;
//import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2AuthenticationInfo;
//import com.codezerotoone.mvp.global.security.token.exception.InvalidAccessTokenException;
//import com.codezerotoone.mvp.global.security.token.support.TokenSupport;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
//import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
//import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Slf4j
//public class DefaultOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
//    private final TokenSupport tokenSupport;
//    private final MemberRepository memberRepository;
//    private final RoleService roleService;
//
//    @Override
//    public OAuth2AuthenticatedPrincipal introspect(String token) {
//        try {
//            OAuth2AuthenticationInfo userInfo = this.tokenSupport.authenticate(token);
//            String oidcId = userInfo.id();
//            Optional<Member> memberOp = this.memberRepository.findByOdicId(oidcId);
//
//            if (memberOp.isPresent()) {
//                Member member = memberOp.get();
//
//                return new DefaultOAuth2AuthenticatedPrincipal(
//                        String.valueOf(member.getMemberId()),
//                        Map.of("sub", oidcId == null ? "" : oidcId),
//                        List.of(new SimpleGrantedAuthority(member.getRole().getRoleId()))
//                );
//            }
//
//            // If memberOp is empty
//            // 로그인을 한 회원이지만, 가입된 회원이 아닐 경우
//            return new DefaultOAuth2AuthenticatedPrincipal(
//                    "-1",
//                    Map.of("sub", oidcId == null ? "" : oidcId),
//                    List.of(new SimpleGrantedAuthority(PrimaryRole.ROLE_GUEST.name()))
//            );
//        } catch (InvalidAccessTokenException e) {
//            return new DefaultOAuth2AuthenticatedPrincipal(
//                    "-1",
//                    Map.of("sub", ""),
//                    List.of(new SimpleGrantedAuthority(PrimaryRole.ROLE_ANONYMOUS.name()))
//            );
////            throw new BadCredentialsException("Invalid access token", e);
//        }
//    }
//}
