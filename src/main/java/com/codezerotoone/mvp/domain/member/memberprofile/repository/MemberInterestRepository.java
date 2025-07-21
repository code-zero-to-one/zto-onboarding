package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    @Query("""
            SELECT i
            FROM MemberInterest i
            WHERE i.memberInterestId IN :memberInterestIds
            """)
    List<MemberInterest> findByMemberInterestIds(@Param("memberInterestIds") Collection<Long> memberInterestIds);

    @Modifying
    @Query("""
            DELETE MemberInterest m
            WHERE m.memberInterestId IN :ids
            """)
    void deleteByIds(@Param("ids") Collection<Long> ids);

    @Modifying
    @Query("""
            DELETE MemberInterest m
            WHERE m.memberProfile.memberId = :memberId
            """)
    void deleteByMemberId(@Param("memberId") Long memberId);
}
