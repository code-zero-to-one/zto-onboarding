package com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaMemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    @Query("""
            SELECT m
            FROM MemberProfile m
            LEFT JOIN Image i ON i.id = m.memberProfileData.profileImage.id
            WHERE m.member.deleteYn = false
                AND m.member.id = :memberId
            """)
    Optional<MemberProfile> findNotDeletedMemberProfileById(@Param("memberId") Long memberId);
}
