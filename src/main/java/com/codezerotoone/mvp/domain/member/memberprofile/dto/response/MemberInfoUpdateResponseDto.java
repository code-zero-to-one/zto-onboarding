package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRef;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.AvailableStudyTime;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.global.util.NullSafetyUtils;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoUpdateResponseDto(
        Long memberId,
        String selfIntroduction,
        String studyPlan,
        String preferredStudySubjectId,
        List<Long> techStackIds,
        List<Long> availableStudyTimeIds
) {

    public static MemberInfoUpdateResponseDto of(MemberProfile memberProfile) {
        return MemberInfoUpdateResponseDto.builder()
                .memberId(memberProfile.getMemberId())
                .selfIntroduction(memberProfile.getMemberInfo().getSelfIntroduction())
                .studyPlan(memberProfile.getMemberInfo().getStudyPlan())
                .preferredStudySubjectId(
                        NullSafetyUtils.extractSafely(memberProfile,
                                (mp) -> mp.getMemberInfo().getPreferredStudySubject().getStudySubjectId()))
                .techStackIds(
                        memberProfile.getMemberInfo()
                                .getTechStackRefs()
                                .stream()
                                .map(TechStackRef::getTechStack)
                                .map(TechStack::getTechStackId)
                                .toList()
                )
                .availableStudyTimeIds(
                        memberProfile.getMemberInfo()
                                .getAvailableStudyTimes()
                                .stream()
                                .map(AvailableStudyTime::getAvailableStudyTimeId)
                                .toList()
                )
                .build();
    }
}
