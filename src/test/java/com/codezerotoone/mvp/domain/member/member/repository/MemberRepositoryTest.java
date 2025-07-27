package com.codezerotoone.mvp.domain.member.member.repository;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.repository.RoleRepository;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Role memberRole = Role.getMemberRole();
        this.roleRepository.save(memberRole);
    }

    @Test
    @DisplayName("existsByLoginId - 회원이 존재할 경우 true 반환")
    void existsByLoginId_returnTrue() {
        // Given
        List<Member> members = List.of(
                Member.createGeneralMember(
                        "sample1",
                        "안유진"
                ),
                Member.createGeneralMember(
                        "sample2",
                        "이서"
                ),
                Member.createGeneralMember(
                        "sample3",
                        "가을"
                ),
                Member.createGeneralMember(
                        "sample4",
                        "장원영"
                ),
                Member.createGeneralMember(
                        "sample5",
                        "레이"
                ),
                Member.createGeneralMember(
                        "sample6",
                        "리즈"
                )
        );

        members.forEach((m) ->
                log.info("loginId={}, deletedAt={}", m.getLoginId(), m.getDeletedAt()));

        this.memberRepository.saveAll(members);

        // When
        boolean result = this.memberRepository.existsByLoginId("sample2");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByLoginId - loginId가 존재하지 않을 경우 false 반환")
    void existsByLoginId_returnFalse() {
        // Given
        List<Member> members = List.of(
                Member.createGeneralMember(
                        "sample1",
                        "안유진"
                ),
                Member.createGeneralMember(
                        "sample2",
                        "이서"
                ),
                Member.createGeneralMember(
                        "sample3",
                        "가을"
                ),
                Member.createGeneralMember(
                        "sample4",
                        "장원영"
                ),
                Member.createGeneralMember(
                        "sample5",
                        "레이"
                ),
                Member.createGeneralMember(
                        "sample6",
                        "리즈"
                )
        );

        this.memberRepository.saveAll(members);

        // When
        boolean result = this.memberRepository.existsByLoginId("sample7");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원이 존재할 경우 ODIC ID로 존재 여부 체크 시 true")
    void existsByOdicId_returnTrue() {
        // Given
        final String odicId = "awoihefiwavipsachpivzx";

        Member member = Member.createGeneralMemberBySocialLogin(
                "이서",
                odicId
        );

        this.memberRepository.save(member);

        // When
        boolean result = this.memberRepository.existsNotDeletedMemberByOidcId(odicId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 ODIC ID로 존재 여부 체크 시 false")
    void existsByOdicId_returnFalse() {
        // Given
        final String odicId = "awoihefiwavipsachpivzx";

        // When
        boolean result = this.memberRepository.existsNotDeletedMemberByOidcId(odicId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("ODIC ID가 틀렸을 경우 false")
    void existsByOdicId_returnFalse_odicIdIsNotCorrect() {
        // Given
        final String odicId = "awoihefiwavipsachpivzx";

        Member member = Member.createGeneralMemberBySocialLogin(
                "이서",
                odicId
        );

        this.memberRepository.save(member);

        // When
        boolean result = this.memberRepository.existsNotDeletedMemberByOidcId(odicId + "as");

        // Then
        assertThat(result).isFalse();
    }
}
