package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa.JpaMemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DefaultMemberProfileRepository implements MemberProfileRepository {
    private final JpaMemberProfileRepository jpaMemberProfileRepository;

    @Override
    public Optional<MemberProfile> findNotDeletedMemberProfileById(Long id) {
        return this.jpaMemberProfileRepository.findNotDeletedMemberProfileById(id);
    }
}
