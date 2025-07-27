package com.codezerotoone.mvp.domain.member.member.repository.extend;

import com.codezerotoone.mvp.domain.member.member.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DefaultExtendedMemberRepository implements ExtendedMemberRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QMember member = QMember.member;

    @Override
    public boolean existsNotDeletedMemberByOidcId(String oidcId) {
        return countByOidcId(this.member.oidcId.eq(oidcId).and(member.deleteYn.eq(false)))
                .orElse(0L) > 0;
    }

    @Override
    public boolean existsByOidcId(String oidcId) {
        return countByOidcId(this.member.oidcId.eq(oidcId))
                .orElse(0L) > 0;
    }

    private Optional<Long> countByOidcId(BooleanExpression condition) {
        return Optional.ofNullable(this.jpaQueryFactory.select(member.count())
                .from(this.member)
                .where(condition)
                .fetchOne());
    }
}
