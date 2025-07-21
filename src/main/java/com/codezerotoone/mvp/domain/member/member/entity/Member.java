package com.codezerotoone.mvp.domain.member.member.entity;

import com.codezerotoone.mvp.domain.common.BaseEntity;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.constant.MemberStatus;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 정보를 나타내는 클래스.
 *
 * @author PGD
 */
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "oidc_id", unique = true)
    private String oidcId; // OpenID Connect에 따라 발급받은 id

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", columnDefinition = "VARCHAR(8)")
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    // private Role role = Role.getMemberRole();
    /**
     * private Role role = Role.getMemberRole();
     * 방식은 Transient 상태의 객체를 즉시 할당하려고 해서 null이 할당하려고 하게 되고
     * null할당 불가 에러가 뜨는 것 같습니다.
     * could not execute statement [(conn=126) Column 'role_id' cannot be null]
     * [insert into member (auto_matching,created_at,deleted_at,login_id,member_status,
     * name,odic_id,phone,role_id,updated_at) values (?,?,?,?,?,?,?,?,?,?)];
     * SQL [insert into member (auto_matching,created_at,deleted_at,login_id,
     * member_status,name,odic_id,phone,role_id,updated_at)
     * values (?,?,?,?,?,?,?,?,?,?)]; constraint [null]
     */

    private LocalDateTime deletedAt = null;

    @OneToOne(mappedBy = "member", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private MemberProfile memberProfile;

    private Member(Long memberId) {
        this.memberId = memberId;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String loginId, MemberProfile memberProfile, String oidcId, Role role) {
        this.loginId = loginId;
        this.memberProfile = memberProfile;
        this.oidcId = oidcId;
        this.role = role;
    }

    public static Member createGeneralMember(String loginId, String memberName) {
        Member newMember = Member.builder()
                .loginId(loginId)
                .role(Role.getMemberRole())
                .build();

        newMember.memberProfile = MemberProfile.requiredBuilder(newMember, memberName).build();
        return newMember;
    }

    public static Member createGeneralMemberBySocialLogin(String memberName,
                                                          String oidcId) {
        Member newMember = Member.builder()
                .oidcId(oidcId)
                .role(Role.getMemberRole())
                .build();

        newMember.memberProfile = MemberProfile.requiredBuilder(newMember, memberName).build();
        return newMember;
    }

    public static Member getReference(Long memberId) {
        return new Member(memberId);
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
