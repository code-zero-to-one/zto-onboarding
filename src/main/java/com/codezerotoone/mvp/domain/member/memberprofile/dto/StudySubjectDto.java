package com.codezerotoone.mvp.domain.member.memberprofile.dto;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.StudySubject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class StudySubjectDto {
    private final String studySubjectId;
    private final String name;

    public static StudySubjectDto of(StudySubject studySubject) {
        if (studySubject == null) {
            return null;
        }
        return new StudySubjectDto(studySubject.getStudySubjectId(), studySubject.getStudySubjectName());
    }
}
