package com.codezerotoone.mvp.domain.member.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * <p>회원의 권한 정보를 저장하는 entity.
 * <p>Role.roleId는 <code>ROLE_</code>로 시작한다.
 *
 * @author PGD
 */
@Entity
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Role {
    private static final String ROLE_PREFIX = "ROLE_";

    @Id
    private String roleId;

    private String roleName;

    @Transient
    private String code;

    private Role(String code) {
        this.roleId = ROLE_PREFIX + code;
        this.code = code;
    }

    /**
     * <code>Role</code> 객체를 생성하는 정적 팩토리 메소드.
     *
     * @param code <p>생성하고자 하는 Role의 code명. ex) MEMBER
     *             <p><code>null</code>일 수 없으며, <code>ROLE_</code>로 시작해서는 안 된다.
     * @return <code>roleId</code>가 <code>ROLE_</code> + UPPERCASE(code)로 변환된 <code>Role</code> 객체. 만약 <code>code</code>가
     * <code>Member</code>일 경우, 반환되는 <code>Role</code> 객체의 <code>roleId</code>의 값은
     * <code>ROLE_MEMBER</code>가 된다.
     * @throws NullPointerException <code>code</code>가 <code>null</code>일 경우
     * @throws IllegalArgumentException <code>code</code>가 <code>ROLE_</code>로 시작할 경우
     */
    public static Role of(String code) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(code);
        if (code.startsWith("ROLE_")) {
            throw new IllegalArgumentException("code must not start with ROLE_: roleId=" + code);
        }
        return new Role(code);
    }

    public static Role getMemberRole() {
        return new Role("MEMBER");
    }

    public static Role getGuestRole() {
        return new Role("GUEST");
    }

    public String getCode() {
        if (this.code == null) {
            this.code = this.roleId.substring(ROLE_PREFIX.length());
        }
        return this.code;
    }

    @Override
    public String toString() {
        return "[ Role=\"" + this.roleId + "\" ]";
    }
}
