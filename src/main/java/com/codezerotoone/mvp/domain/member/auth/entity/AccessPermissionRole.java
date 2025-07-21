package com.codezerotoone.mvp.domain.member.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "access_permission_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccessPermissionRole {

    @Id
    private Long accessPermissionRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    private AccessPermission accessPermission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}
