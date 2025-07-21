package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: study 패키지 아래 들어가야 할지도?
@Entity
@Table(name = "study_subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudySubject {

    @Id
    @Column(name = "study_subject_id", columnDefinition = "VARCHAR(15)")
    private String studySubjectId;

    private String studySubjectName;

    public static StudySubject getReference(String studySubjectId) {
        StudySubject studySubject = new StudySubject();
        studySubject.studySubjectId = studySubjectId;
        return studySubject;
    }
}
