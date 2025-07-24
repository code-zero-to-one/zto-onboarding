package com.codezerotoone.mvp.domain.member.member.dto;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.constant.MemberStatus;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {

    private Long memberId;
    private String oidcId;
    private String loginId;
    private MemberStatus memberStatus;
    private String role;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .memberId(member.getMemberId())
                .oidcId(member.getOidcId())
                .loginId(member.getLoginId())
                .memberStatus(member.getMemberStatus())
                .role(member.getRole().getRoleId())
                .build();
    }
}
