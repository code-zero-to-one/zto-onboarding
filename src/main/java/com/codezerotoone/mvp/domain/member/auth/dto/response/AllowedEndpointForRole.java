package com.codezerotoone.mvp.domain.member.auth.dto.response;

import com.codezerotoone.mvp.domain.member.auth.dto.RoleDto;

import java.util.List;

public record AllowedEndpointForRole(String endpoint, List<RoleDto> roles) {
}
