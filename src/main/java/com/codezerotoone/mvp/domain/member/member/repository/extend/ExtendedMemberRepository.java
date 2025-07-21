package com.codezerotoone.mvp.domain.member.member.repository.extend;

import org.springframework.data.repository.query.Param;

public interface ExtendedMemberRepository {

    boolean existsNotDeletedMemberByOidcId(@Param("oidcId") String oidcId);

    boolean existsByOidcId(@Param("oidcId") String oidcId);
}
