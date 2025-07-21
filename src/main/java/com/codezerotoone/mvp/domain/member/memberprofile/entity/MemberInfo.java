package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRef;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRefType;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberInfoAtomicUpdateDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class MemberInfo {

    /**
     * 자기소개
     */
    private String selfIntroduction;

    /**
     * 공부 주제 및 계획
     */
    private String studyPlan;

    /**
     * 선호하는 스터디 주제
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_study_subject_id")
    private StudySubject preferredStudySubject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "profile_avl_time",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "available_study_time_id")
    )
    private List<AvailableStudyTime> availableStudyTimes = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechStackRef> techStackRefs = new ArrayList<>();

    public static MemberInfo createEmpty() {
        return new MemberInfo();
    }

    public void updateAtomicValues(MemberInfoAtomicUpdateDto dto, boolean ignoreNull) {
        if (!ignoreNull || dto.selfIntroduction() != null) {
            this.selfIntroduction = dto.selfIntroduction();
        }

        if (!ignoreNull || dto.studyPlan() != null) {
            this.studyPlan = dto.studyPlan();
        }

        if (!ignoreNull || dto.preferredStudySubject() != null) {
            this.preferredStudySubject = dto.preferredStudySubject();
        }
    }

    public void replaceAvailableStudyTimes(List<Long> availableStudyTimeIds) {
        if (availableStudyTimeIds != null) {
            this.availableStudyTimes.clear();
            this.availableStudyTimes.addAll(
                    availableStudyTimeIds.stream()
                            .map(AvailableStudyTime::getReference)
                            .toList()
            );
        }
    }

    public void replaceTechStacks(List<Long> techStackIds, MemberProfile memberProfile) {
        if (techStackRefs != null) {
            this.techStackRefs.clear();
            this.techStackRefs.addAll(
                    techStackIds.stream()
                            .map((id) -> TechStackRef.create(
                                    TechStack.getReference(id),
                                    memberProfile,
                                    TechStackRefType.MEMBER
                            ))
                            .toList()
            );
        }
    }
}
