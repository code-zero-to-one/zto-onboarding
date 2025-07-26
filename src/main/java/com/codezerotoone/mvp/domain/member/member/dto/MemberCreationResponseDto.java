package com.codezerotoone.mvp.domain.member.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberCreationResponseDto(

        @Schema(description = "회원가입 시 자동 생성되는 memberId. loginId와 다르며, 회원을 식별하는 데 사용된다.")
        Long generatedMemberId,

        @Schema(description = "프로필 이미지를 업로드할 URL")
        String uploadUrl
) {
        public static MemberCreationResponseDto of(Long memberId) {
                return new MemberCreationResponseDto(memberId, null);
        }
}
