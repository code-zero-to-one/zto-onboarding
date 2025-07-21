package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.SocialMedia;
import com.codezerotoone.mvp.global.util.NullSafetyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record MemberProfileForStudyDto(

        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "자기소개")
        String selfIntroduction,

        @Schema(description = "공부 주제 및 계획")
        String studyPlan,

        @Schema(description = "선호하는 스터디 주제 ID")
        String preferredStudySubjectId,

        @Schema(description = "가능 시간대 ID 리스트")
        List<Long> availableStudyTimeIds,

        @Schema(description = "사용 가능한 기술 스택 리스트")
        List<Long> availableTechStackIds,

        @Schema(description = "연락처")
        String tel,

        @Schema(description = "GitHub 링크")
        SocialMediaResponseDto gitHubLink,

        @Schema(description = "블로그/SNS 등 링크")
        SocialMediaResponseDto blogOrSnsLink
) {

    public static MemberProfileForStudyDto of(MemberProfile memberProfile) {
        SocialMedia github = memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.GITHUB);
        SocialMedia blogOrSns =
                memberProfile.getMemberProfileData().getPrimarySocialMedia(PrimarySocialMediaType.BLOG_OR_SNS);
        return MemberProfileForStudyDto.builder()
                .memberId(memberProfile.getMemberId())
                .selfIntroduction(memberProfile.getMemberInfo().getSelfIntroduction())
                .studyPlan(memberProfile.getMemberInfo().getStudyPlan())
                .availableStudyTimeIds(memberProfile.getMemberInfo()
                        .getAvailableStudyTimes()
                        .stream()
                        .map(AvailableStudyTime::getAvailableStudyTimeId)
                        .toList())
                .preferredStudySubjectId(
                        NullSafetyUtils.extractSafely(memberProfile, (mp) ->
                                mp.getMemberInfo().getPreferredStudySubject().getStudySubjectId())
                )
                .availableTechStackIds(memberProfile.getMemberInfo()
                        .getTechStackRefs()
                        .stream()
                        .map((ref) -> ref.getTechStack().getTechStackId())
                        .toList())
                .gitHubLink(
                        NullSafetyUtils.extractSafely(github, (gh) ->
                                SocialMediaResponseDto.builder()
                                        .socialMediaId(github.getSocialMediaId())
                                        .url(github.getUrl())
                                        .iconUrl(null) // TODO
                                        .type(github.getSocialMediaType().getSocialMediaTypeId())
                                        .build())
                )
                .blogOrSnsLink(
                        NullSafetyUtils.extractSafely(blogOrSns, (bos) ->
                                SocialMediaResponseDto.builder()
                                        .socialMediaId(blogOrSns.getSocialMediaId())
                                        .url(blogOrSns.getUrl())
                                        .iconUrl(null) // TODO
                                        .type(blogOrSns.getSocialMediaType().getSocialMediaTypeId())
                                        .build())
                )
                .tel(memberProfile.getMemberProfileData().getTel())
                .build();
    }
}
