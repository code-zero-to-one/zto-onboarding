//package com.codezerotoone.mvp.domain.member.auth.service;
//
//import com.codezerotoone.mvp.domain.member.auth.dto.response.LoginResult;
//import com.codezerotoone.mvp.domain.member.member.entity.Member;
//import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
//import com.codezerotoone.mvp.global.security.token.dto.GrantedTokenInfo;
//import com.codezerotoone.mvp.global.security.token.dto.OAuth2UserInfo;
//import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
//import com.codezerotoone.mvp.global.security.token.support.TokenSupport;
//import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AuthService {
//    private final TokenSupport tokenSupport;
//    private final MemberRepository memberRepository;
//
//    @Transactional
//    public LoginResult loginByOAuth2(String code, String redirectUri, AuthVendor authVendor)
//            throws UnsupportedCodeException {
//        GrantedTokenInfo grantedTokenInfo = this.tokenSupport.grantToken(code, redirectUri, authVendor);
//        Optional<Member> memberOp = this.memberRepository.findByOdicId(grantedTokenInfo.id());
//        if (memberOp.isPresent()) {
//            Member member = memberOp.get();
//            return LoginResult.builder()
//                    .newMember(false)
//                    .accessToken(grantedTokenInfo.accessToken())
//                    .refreshToken(grantedTokenInfo.refreshToken())
//                    .memberId(member.getMemberId())
//                    .build();
//        }
//        OAuth2UserInfo userInfo = this.tokenSupport.retrieveUserInfo(grantedTokenInfo.accessToken());
//        return LoginResult.builder()
//                .newMember(true)
//                .accessToken(grantedTokenInfo.accessToken())
//                .refreshToken(grantedTokenInfo.refreshToken())
//                .profileImageUrl(userInfo.profileImageUrl())
//                .userName(userInfo.name())
//                .build();
//    }
//}
