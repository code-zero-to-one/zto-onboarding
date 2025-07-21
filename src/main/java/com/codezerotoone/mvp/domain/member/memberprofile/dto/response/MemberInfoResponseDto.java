package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.category.techstack.dto.response.TechStackResponse;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.AvailableStudyTimeDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.StudySubjectDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoResponseDto(

        @Schema(description = "자기소개 (긴 거)")
        String selfIntroduction,

        @Schema(description = "공부 계획")
        String studyPlan,

        @Schema(description = "선호하는 스터디 주제")
        StudySubjectDto preferredStudySubject,

        @Schema(description = "스터디 가능 시간대")
        List<AvailableStudyTimeDto> availableStudyTimes,

        @Schema(description = "기술스택")
        List<TechStackResponse> techStacks
) {

    public static MemberInfoResponseDto of(MemberInfo memberInfo) {
        return MemberInfoResponseDto.builder()
                .selfIntroduction(memberInfo.getSelfIntroduction())
                .studyPlan(memberInfo.getStudyPlan())
                .preferredStudySubject(StudySubjectDto.of(memberInfo.getPreferredStudySubject()))
                .availableStudyTimes(memberInfo.getAvailableStudyTimes()
                        .stream()
                        .map(AvailableStudyTimeDto::of)
                        .toList())
                .techStacks(memberInfo.getTechStackRefs()
                        .stream()
                        .map((ref) -> {
                            TechStack techStack = ref.getTechStack();
                            return new TechStackResponse(
                                    techStack.getTechStackId(),
                                    techStack.getTechStackName(),
                                    techStack.getParent() == null ? null : techStack.getParent().getTechStackId(),
                                    techStack.getLevel()
                            );
                        })
                        .toList())
                .build();
    }
}
