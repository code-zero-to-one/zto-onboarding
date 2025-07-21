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

    public static MemberInterest create(MemberProfile memberProfile, String name) {
        MemberInterest memberInterest = new MemberInterest();
        memberInterest.memberProfile = memberProfile;
        memberInterest.name = name;
        return memberInterest;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void detachMemberProfile() {
        this.memberProfile = null;
    }
}
