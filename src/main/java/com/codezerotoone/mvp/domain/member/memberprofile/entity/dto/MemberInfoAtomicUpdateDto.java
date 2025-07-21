package com.codezerotoone.mvp.domain.member.memberprofile.entity.dto;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.StudySubject;

public record MemberInfoAtomicUpdateDto(
        String selfIntroduction,
        String studyPlan,
        StudySubject preferredStudySubject
) {
}
