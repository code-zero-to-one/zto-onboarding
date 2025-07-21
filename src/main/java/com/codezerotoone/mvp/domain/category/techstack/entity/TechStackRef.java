package com.codezerotoone.mvp.domain.category.techstack.entity;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기술스택 참조(연결) 엔티티
 *
 * <p>회원(Member)과 기술스택(TechStack) 간의 관계를 표현합니다.
 * 예를 들어, 사용자가 "스터디 신청" 시 선택한 기술스택을 관리합니다.
 * 또한 type 필드를 통해 어떤 용도로 사용되는 기술스택인지 구분할 수 있습니다.</p>
 */
@Entity
@Table(name = "tech_stack_ref")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TechStackRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long techStackRefId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tech_stack_id")
    private TechStack techStack;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "member_id")
    private MemberProfile memberProfile;

    /**
     * 기술스택 참조 타입
     *
     * <p>어떤 용도의 기술스택인지 구분합니다.
     * 예) STUDY(스터디 신청), 추후 PROJECT(프로젝트 기술스택) 등으로 확장 가능</p>
     */
    @Enumerated(EnumType.STRING)
    private TechStackRefType type;

    private TechStackRef(TechStack techStack, MemberProfile memberProfile, TechStackRefType type) {
        this.techStack = techStack;
        this.memberProfile = memberProfile;
        this.type = type;
    }

    public static TechStackRef create(TechStack techStack, MemberProfile memberProfile, TechStackRefType type) {
        return new TechStackRef(techStack, memberProfile, type);
    }
}
