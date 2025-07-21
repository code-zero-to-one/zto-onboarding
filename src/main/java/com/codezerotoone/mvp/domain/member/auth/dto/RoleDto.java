package com.codezerotoone.mvp.domain.member.auth.dto;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class RoleDto {
    private final String roleId;
    private final String roleName;
    private final String code;

    public static RoleDto of(Role role) {
        return new RoleDto(role.getRoleId(), role.getRoleName(), role.getCode());
    }
}
