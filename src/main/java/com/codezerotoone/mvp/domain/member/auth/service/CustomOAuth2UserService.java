package com.codezerotoone.mvp.domain.member.auth.service;

import com.codezerotoone.mvp.domain.member.auth.dto.CustomOAuth2User;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.dto.MemberDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import com.codezerotoone.mvp.global.security.token.support.OAuth2Response;
import com.codezerotoone.mvp.global.security.token.vendor.AuthSocial;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: " + oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        AuthSocial social = AuthSocial.fromName(registrationId);
        OAuth2Response oAuth2Response = social.createResponse(oAuth2User.getAttributes());
        String oidcId = oAuth2Response.getProviderId();

        Member existData = memberRepository.findByOdicId(oidcId).orElse(null);

        if (existData != null) {
            MemberDto userDto = MemberDto.fromEntity(existData);

            return new CustomOAuth2User(userDto);
        } else {
            Member member = Member.createGeneralMemberBySocialLogin("프로필이름", oidcId);
            memberRepository.save(member);

            MemberDto memberDto = MemberDto.fromEntity(member);
            return new CustomOAuth2User(memberDto);
        }
    }
}
