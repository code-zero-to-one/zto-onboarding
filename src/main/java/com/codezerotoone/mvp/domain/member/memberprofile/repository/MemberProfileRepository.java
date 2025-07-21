package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;

import java.util.Optional;

public interface MemberProfileRepository {

    Optional<MemberProfile> findNotDeletedMemberProfileById(Long id);
}
