package com.codezerotoone.mvp.domain.member.auth.service;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.dto.response.AllowedEndpointForRole;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermissionRole;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.repository.AccessPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RoleServiceTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    AccessPermissionRepository accessPermissionRepository;

    @Test
    @DisplayName("HTTP Method에 따라서 엔드포인트별 접근 권한 정보를 반환한다.")
    void getAllAccessPermission_success() throws Exception {
        // Given
        Role roleMember = generateRole("ROLE_MEMBER", "Member");
        Role roleGuest = generateRole("ROLE_GUEST", "Guest");

        AccessPermission accessPermission1 = generateAccessPermission(1L, "/api/v1/test1", AuthorizedHttpMethod.GET);
        AccessPermission accessPermission2 = generateAccessPermission(2L, "/api/v1/test2", AuthorizedHttpMethod.GET);
        AccessPermission accessPermission3 = generateAccessPermission(3L, "/api/v1/test3", AuthorizedHttpMethod.GET);

        AccessPermissionRole accessPermissionRole1 = generateAccessPermissionRole(1L, accessPermission1, roleMember);
        AccessPermissionRole accessPermissionRole2 = generateAccessPermissionRole(2L, accessPermission2, roleMember);
        AccessPermissionRole accessPermissionRole3 = generateAccessPermissionRole(3L, accessPermission3, roleMember);
        AccessPermissionRole accessPermissionRole4 = generateAccessPermissionRole(4L, accessPermission1, roleGuest);
        AccessPermissionRole accessPermissionRole5 = generateAccessPermissionRole(5L, accessPermission2, roleGuest);

        accessPermission1.getAccessPermissionRole().add(accessPermissionRole1);
        accessPermission1.getAccessPermissionRole().add(accessPermissionRole4);
        accessPermission2.getAccessPermissionRole().add(accessPermissionRole2);
        accessPermission2.getAccessPermissionRole().add(accessPermissionRole5);
        accessPermission3.getAccessPermissionRole().add(accessPermissionRole3);

        when(this.accessPermissionRepository.findAll())
                .thenReturn(List.of(
                        accessPermission1, accessPermission2, accessPermission3
                ));

        // When
        Map<AuthorizedHttpMethod, List<AllowedEndpointForRole>> result = this.roleService.getAllAccessPermission();

        log.info("result={}", result);

        // Then
        assertThat(result).size().isEqualTo(1);
        assertThat(result).containsKey(AuthorizedHttpMethod.GET);

        List<AllowedEndpointForRole> allowedEndpointForRoles = result.get(AuthorizedHttpMethod.GET);
        assertThat(allowedEndpointForRoles).extracting("endpoint")
                .containsExactlyInAnyOrder("/api/v1/test1", "/api/v1/test2", "/api/v1/test3");

        for (AllowedEndpointForRole allowedEndpointForRole : allowedEndpointForRoles) {
            if (allowedEndpointForRole.endpoint().equals("/api/v1/test1")) {
                assertThat(allowedEndpointForRole.roles()).extracting("roleId")
                        .containsExactlyInAnyOrder("ROLE_MEMBER", "ROLE_GUEST");
            } else if (allowedEndpointForRole.endpoint().equals("/api/v1/test2")) {
                assertThat(allowedEndpointForRole.roles()).extracting("roleId")
                        .containsExactlyInAnyOrder("ROLE_MEMBER", "ROLE_GUEST");
            } else if (allowedEndpointForRole.endpoint().equals("/api/v1/test3")) {
                assertThat(allowedEndpointForRole.roles()).extracting("roleId")
                        .containsExactlyInAnyOrder("ROLE_MEMBER");
            } else {
                fail();
            }
        }
    }

    private AccessPermission generateAccessPermission(Long permissionId, String endpoint, AuthorizedHttpMethod httpMethod)
            throws Exception {
        AccessPermission accessPermission = instantiate(AccessPermission.class);
        ReflectionUtils.setField(AccessPermission.class.getDeclaredField("permissionId"), accessPermission, permissionId);
        ReflectionUtils.setField(AccessPermission.class.getDeclaredField("endpoint"), accessPermission, endpoint);
        ReflectionUtils.setField(AccessPermission.class.getDeclaredField("httpMethod"), accessPermission, httpMethod);
        return accessPermission;
    }

    private AccessPermissionRole generateAccessPermissionRole(Long accessPermissionRoleId,
                                                              AccessPermission accessPermission,
                                                              Role role) throws Exception {
        AccessPermissionRole accessPermissionRole = instantiate(AccessPermissionRole.class);
        ReflectionUtils.setField(AccessPermissionRole.class.getDeclaredField("accessPermissionRoleId"), accessPermissionRole, accessPermissionRoleId);
        ReflectionUtils.setField(AccessPermissionRole.class.getDeclaredField("accessPermission"), accessPermissionRole, accessPermission);
        ReflectionUtils.setField(AccessPermissionRole.class.getDeclaredField("role"), accessPermissionRole, role);
        return accessPermissionRole;
    }

    private Role generateRole(String roleId, String roleName) throws Exception {
        Role role = instantiate(Role.class);
        ReflectionUtils.setField(Role.class.getDeclaredField("roleId"), role, roleId);
        ReflectionUtils.setField(Role.class.getDeclaredField("roleName"), role, roleName);
        return role;
    }

    private <T> T instantiate(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T object = constructor.newInstance();
        constructor.setAccessible(false);
        return object;
    }
}