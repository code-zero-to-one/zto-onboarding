package com.codezerotoone.mvp.domain.member.member.repository;

import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.extend.ExtendedMemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, ExtendedMemberRepository {

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.deleteYn = false
                AND m.id = :memberId
            """)
    Optional<Member> findNotDeletedMemberById(@Param("memberId") Long memberId);

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.deleteYn = false
                AND m.loginId = :loginId
            """)
    Optional<Member> findByLoginId(@Param("loginId") String loginId);

    @Query(value = """
            SELECT COUNT(*) > 0
            FROM member m
            WHERE m.deleteYn = false
                 AND m.login_id = :loginId
            LIMIT 1
            """, nativeQuery = true)
    boolean existsByLoginId(@Param("loginId") String loginId);

    @Query(value = """
            SELECT m
            FROM Member m
            WHERE m.deleteYn = false
                AND m.oidcId = :oidcId
            """)
    Optional<Member> findByOdicId(@Param("oidcId") String oidcId);
}
