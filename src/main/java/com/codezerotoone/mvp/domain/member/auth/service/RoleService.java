package com.codezerotoone.mvp.domain.member.auth.service;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.dto.RoleDto;
import com.codezerotoone.mvp.domain.member.auth.dto.response.AllowedEndpointForRole;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermissionRole;
import com.codezerotoone.mvp.domain.member.auth.repository.AccessPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final AccessPermissionRepository accessPermissionRepository;

    @Transactional(readOnly = true)
    public Map<AuthorizedHttpMethod, List<AllowedEndpointForRole>> getAllAccessPermission() {
        List<AccessPermission> allAccessPermissions = this.accessPermissionRepository.findAll();
        log.info("allAccessPermissions: {}", allAccessPermissions.size());

        Map<AuthorizedHttpMethod, List<AllowedEndpointForRole>> accessPermissionPerHttpMethod = new HashMap<>();

        for (AccessPermission accessPermission : allAccessPermissions) {
            if (!accessPermissionPerHttpMethod.containsKey(accessPermission.getHttpMethod())) {
                accessPermissionPerHttpMethod.put(accessPermission.getHttpMethod(), new ArrayList<>());
            }
            accessPermissionPerHttpMethod.get(accessPermission.getHttpMethod())
                    .add(new AllowedEndpointForRole(accessPermission.getEndpoint(),
                            accessPermission.getAccessPermissionRole()
                                    .stream()
                                    .map(AccessPermissionRole::getRole)
                                    .map(RoleDto::of)
                                    .toList()));
        }

        return accessPermissionPerHttpMethod;
    }
}
