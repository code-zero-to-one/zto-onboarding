package com.codezerotoone.mvp.domain.member.memberprofile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MemberInfoUpdateRequestDto {

    @Schema(description = "자기소개")
    private String selfIntroduction;

    @Schema(description = "공부 주제 및 계획")
    private String studyPlan;

    @Schema(description = "선호하는 스터디 주제 ID")
    private String preferredStudySubjectId; // TODO: selection으로 변경

    @Schema(description = "가능 시간대")
    private List<Long> availableStudyTimeIds;

    @Schema(description = "사용 가능한 기술스택 ID 리스트")
    private List<Long> techStackIds;
}
