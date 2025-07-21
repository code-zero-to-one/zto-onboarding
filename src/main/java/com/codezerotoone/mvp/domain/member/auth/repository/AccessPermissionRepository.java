package com.codezerotoone.mvp.domain.member.auth.repository;

import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.repository.querydsl.QueryDslAccessPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccessPermissionRepository {
    private final QueryDslAccessPermissionRepository queryDslAccessPermissionRepository;

    public List<AccessPermission> findAll() {
        return this.queryDslAccessPermissionRepository.findAll();
    }
}
