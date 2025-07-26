package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;


import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.IdNameDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.SocialMedia;
import com.codezerotoone.mvp.global.util.NullSafetyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberProfileUpdateResponseDto(

        @Schema(description = "업데이트된 회원의 ID")
        Long memberId,

        @Schema(description = "업데이트된 회원의 이름")
        String name,

        @Schema(description = "프로필 이미지 업로드 URL. 이 URL로 프로필 이미지를 업로드하면 완료된다.")
        String profileImageUploadUrl,

        @Schema(description = "연락처")
        String tel,

        @Schema(description = "업데이트된 회원의 github 링크")
        String githubLink,

        @Schema(description = "업데이트된 회원의 블로그/SNS 링크")
        String blogOrSnsLink,

        @Schema(description = "업데이트된 한 마디 소개")
        String simpleIntroduction,

        @Schema(description = "업데이트된 MBTI")
        Mbti mbti,

        @Schema(description = "업데이트된 관심사")
        List<IdNameDto> interests
) {

        public static MemberProfileUpdateResponseDto from(MemberProfile memberProfile) {
                return MemberProfileUpdateResponseDto.builder()
                        .memberId(memberProfile.getMemberId())
                        .name(memberProfile.getMemberName())
                        .tel(memberProfile.getMemberProfileData().getTel())
                        .githubLink(NullSafetyUtils.extractSafely(
                                memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.GITHUB),
                                SocialMedia::getUrl))
                        .blogOrSnsLink(NullSafetyUtils.extractSafely(
                                memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.BLOG_OR_SNS),
                                SocialMedia::getUrl))
                        .simpleIntroduction(memberProfile.getMemberProfileData().getSimpleIntroduction())
                        .mbti(memberProfile.getMemberProfileData().getMbti())
                        .interests(
                                memberProfile.getMemberProfileData()
                                        .getMemberInterests()
                                        .stream()
                                        .map((mi) -> new IdNameDto(mi.getMemberInterestId(), mi.getName()))
                                        .toList()
                        )
                        .build();
        }

        public static MemberProfileUpdateResponseDto from(MemberProfile memberProfile, String profileImageUploadUrl) {
                return MemberProfileUpdateResponseDto.builder()
                        .memberId(memberProfile.getMemberId())
                        .name(memberProfile.getMemberName())
                        .profileImageUploadUrl(profileImageUploadUrl)
                        .tel(memberProfile.getMemberProfileData().getTel())
                        .githubLink(NullSafetyUtils.extractSafely(
                                memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.GITHUB),
                                SocialMedia::getUrl))
                        .blogOrSnsLink(NullSafetyUtils.extractSafely(
                                memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.BLOG_OR_SNS),
                                SocialMedia::getUrl))
                        .simpleIntroduction(memberProfile.getMemberProfileData().getSimpleIntroduction())
                        .mbti(memberProfile.getMemberProfileData().getMbti())
                        .interests(
                                memberProfile.getMemberProfileData()
                                        .getMemberInterests()
                                        .stream()
                                        .map((mi) -> new IdNameDto(mi.getMemberInterestId(), mi.getName()))
                                        .toList()
                        )
                        .build();
        }
}
