package com.codezerotoone.mvp.domain.member.auth.repository.querydsl;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermissionRole;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Slf4j
class QueryDslAccessPermissionRepositoryTest {

    @Autowired
    QueryDslAccessPermissionRepository queryDslAccessPermissionRepository;

    @Autowired
    EntityManager em;

    @Test
    void findAll_success() throws Exception {
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

        this.em.persist(roleMember);
        this.em.persist(roleGuest);

        this.em.persist(accessPermission1);
        this.em.persist(accessPermission2);
        this.em.persist(accessPermission3);

        this.em.persist(accessPermissionRole1);
        this.em.persist(accessPermissionRole2);
        this.em.persist(accessPermissionRole3);
        this.em.persist(accessPermissionRole4);
        this.em.persist(accessPermissionRole5);

        this.em.flush();
        this.em.clear();

        // When
        List<AccessPermission> result = this.queryDslAccessPermissionRepository.findAll();

        log.info("result.size()={}", result.size());

        // Then
        assertThat(result).size().isEqualTo(3);

        for (AccessPermission resultAccessPermission : result) {
            if (resultAccessPermission.getPermissionId().equals(accessPermission1.getPermissionId())) {
                assertThat(resultAccessPermission.getAccessPermissionRole()).size().isEqualTo(2);
                assertThat(
                        resultAccessPermission.getAccessPermissionRole()
                                .stream()
                                .map(AccessPermissionRole::getRole)
                                .map(Role::getRoleId)
                ).containsExactlyInAnyOrder(
                        roleMember.getRoleId(), roleGuest.getRoleId()
                );
            } else if (resultAccessPermission.getPermissionId().equals(accessPermission2.getPermissionId())) {
                assertThat(resultAccessPermission.getAccessPermissionRole()).size().isEqualTo(2);
                assertThat(
                        resultAccessPermission.getAccessPermissionRole()
                                .stream()
                                .map(AccessPermissionRole::getRole)
                                .map(Role::getRoleId)
                ).containsExactlyInAnyOrder(
                        roleMember.getRoleId(), roleGuest.getRoleId()
                );
            } else if (resultAccessPermission.getPermissionId().equals(accessPermission3.getPermissionId())) {
                assertThat(resultAccessPermission.getAccessPermissionRole()).size().isEqualTo(1);
                assertThat(
                        resultAccessPermission.getAccessPermissionRole()
                                .stream()
                                .map(AccessPermissionRole::getRole)
                                .map(Role::getRoleId)
                ).containsExactlyInAnyOrder(
                        roleMember.getRoleId()
                );
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