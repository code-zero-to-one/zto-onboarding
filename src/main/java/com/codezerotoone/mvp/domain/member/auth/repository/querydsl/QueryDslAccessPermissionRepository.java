package com.codezerotoone.mvp.domain.member.auth.repository.querydsl;

import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.QAccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.QAccessPermissionRole;
import com.codezerotoone.mvp.domain.member.auth.entity.QRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDslAccessPermissionRepository {
    private final JPAQueryFactory queryFactory;
    private final QRole role = QRole.role;
    private final QAccessPermission accessPermission = QAccessPermission.accessPermission;
    private final QAccessPermissionRole accessPermissionRole = QAccessPermissionRole.accessPermissionRole;

    public List<AccessPermission> findAll() {
        return this.queryFactory.selectFrom(this.accessPermission)
                .innerJoin(this.accessPermissionRole).on(this.accessPermissionRole.accessPermission.eq(this.accessPermission)).fetchJoin()
                .innerJoin(this.role).on(this.role.eq(this.accessPermissionRole.role)).fetchJoin()
                .fetch();
    }
}
