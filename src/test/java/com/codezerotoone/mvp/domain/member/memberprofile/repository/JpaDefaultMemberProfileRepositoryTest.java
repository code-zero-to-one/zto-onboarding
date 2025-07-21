package com.codezerotoone.mvp.domain.member.memberprofile.repository;

import com.codezerotoone.mvp.domain.category.techstack.repository.TechStackRepository;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.repository.RoleRepository;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.repository.jpa.JpaMemberProfileRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
class JpaDefaultMemberProfileRepositoryTest {

    DefaultMemberProfileRepository defaultMemberProfileRepository;

    @Autowired
    JpaMemberProfileRepository jpaMemberProfileRepository;

    @Autowired
    AvailableStudyTimeRepository availableStudyTimeRepository;

    @Autowired
    MemberInterestRepository memberInterestRepository;

    @Autowired
    StudySubjectRepository studySubjectRepository;

    @Autowired
    TechStackRepository techStackRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        this.roleRepository.save(Role.getMemberRole());
        this.roleRepository.save(Role.getGuestRole());
        this.defaultMemberProfileRepository = new DefaultMemberProfileRepository(
                this.jpaMemberProfileRepository
        );
    }

    @Test
    @DisplayName("회원 엔티티를 추가하면 회원 프로필 엔티티도 동시에 추가됨")
    void saveMember() {
        Member member;
        { // Given
            // 기본 정보만 들어간 회원 엔티티 생성
            member = Member.createGeneralMemberBySocialLogin("안유진", "123");
        }

        Long generatedMemberId;
        { // When
            generatedMemberId = this.memberRepository.save(member).getMemberId();
        }

        { // Then
            Optional<MemberProfile> result = this.defaultMemberProfileRepository.findNotDeletedMemberProfileById(generatedMemberId);
            assertThat(result).isNotEmpty();
        }
    }

    @Test
    @DisplayName("회원이 삭제될 경우, 회원 프로필 엔티티가 조회되지 않음")
    void saveMember_delete() {
        Long generatedMemberId;
        { // Given
            // 기본 정보만 들어간 회원 엔티티 생성
            Member member = Member.createGeneralMemberBySocialLogin("안유진", "123");
            member = this.memberRepository.save(member);
            // 회원 프로필 삭제 (영속성 컨텍스트 안에 있는 엔티티에 영향을 미침)
            member.delete();
            generatedMemberId = member.getMemberId();
        }

        Optional<MemberProfile> result;
        { // When
            result = this.defaultMemberProfileRepository.findNotDeletedMemberProfileById(generatedMemberId);
        }

        { // Then
            assertThat(result).isEmpty();
        }
    }
}