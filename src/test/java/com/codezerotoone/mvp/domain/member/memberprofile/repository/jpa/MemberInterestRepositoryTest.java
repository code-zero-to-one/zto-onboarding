package com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInterest;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.MemberInterestRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MemberInterestRepositoryTest {

    @Autowired
    MemberInterestRepository memberInterestRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("delete 성공")
    void delete() {
        // Given
        Role role = Role.getMemberRole();
        this.em.persist(role);
        Member member = Member.createGeneralMember("sample", "안유진");
        this.em.persist(member);

        MemberProfile memberProfile = this.em.find(MemberProfile.class, member.getMemberId());

        List<MemberInterest> memberInterests = List.of(
                MemberInterest.of(memberProfile, "hello"),
                MemberInterest.of(memberProfile, "world")
        );

        memberInterests.forEach(this.em::persist);

        Long toUpdateId = memberInterests.getFirst().getMemberInterestId();
        List<Long> toDeleteIds = memberInterests.stream().map(MemberInterest::getMemberInterestId).toList();

        // When
        this.memberInterestRepository.deleteByIds(toDeleteIds);
        this.em.flush();
        this.em.clear();

        // Then
        List<MemberInterest> find = this.em.createQuery("""
                        SELECT i
                        FROM MemberInterest i
                        WHERE i.memberProfile.memberId = :memberId
                        """, MemberInterest.class)
                .setParameter("memberId", memberProfile.getMemberId())
                .getResultList();
        assertThat(find).isEmpty();
    }
}