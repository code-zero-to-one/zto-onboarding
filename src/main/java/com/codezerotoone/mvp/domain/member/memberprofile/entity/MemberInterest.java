package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_interest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberInterestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberProfile memberProfile;

    private String name;

    private MemberInterest(MemberProfile memberProfile, String name) {
        this.memberProfile = memberProfile;
        this.name = name;
    }

    // 변경 사유: 로직 간소화 및 팩토리 메서드명 수정했습니다.
    public static MemberInterest of(MemberProfile memberProfile, String name) {
        return new MemberInterest(memberProfile, name);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void detachMemberProfile() {
        this.memberProfile = null;
    }
}
