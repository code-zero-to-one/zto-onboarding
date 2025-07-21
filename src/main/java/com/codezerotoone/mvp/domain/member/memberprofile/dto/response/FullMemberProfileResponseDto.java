package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FullMemberProfileResponseDto(

        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "자동 매칭 여부")
        boolean autoMatching,

        @Schema(description = "스터디를 신청했는지 여부")
        boolean studyApplied,

        @Schema(description = "회원 정보")
        MemberInfoResponseDto memberInfo,

        @Schema(description = "회원 프로필")
        MemberProfileResponseDto memberProfile
) {

        public static FullMemberProfileResponseDto of(MemberProfile memberProfile) {
                return FullMemberProfileResponseDto.builder()
                        .memberId(memberProfile.getMemberId())
                        .memberInfo(MemberInfoResponseDto.of(memberProfile.getMemberInfo()))
                        .memberProfile(MemberProfileResponseDto.of(memberProfile.getMemberProfileData()))
                        .build();
        }
}
