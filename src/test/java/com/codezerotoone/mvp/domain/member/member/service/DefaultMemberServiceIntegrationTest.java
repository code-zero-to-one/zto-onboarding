package com.codezerotoone.mvp.domain.member.member.service;

import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.exception.DuplicateMemberException;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@ActiveProfiles("test")
class DefaultMemberServiceIntegrationTest {

    @Autowired
    DefaultMemberService defaultMemberService;

    @Autowired
    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(Role.getMemberRole());
        tx.commit();
    }

    @AfterEach
    void tearDown() {
        EntityManager em = this.emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.find(Role.class, Role.getMemberRole().getRoleId()));
        tx.commit();
    }

    @Test
    @DisplayName("이미 가입된 회원인데 또 가입하려 할 경우 예외 발생")
    @Transactional
    void createMember_DuplicateMemberException() {
        // 회원 미리 생성
        final String oidcId = "123";
        MemberCreationRequestDto preliminaryDto = MemberCreationRequestDto.builder()
                .name("안유진")
                .build();
        this.defaultMemberService.createMember(preliminaryDto, oidcId);

        // 이미 사용된 OICD ID를 가지고 회원 다시 생성을 시도할 때 사용될 Request Dto
        MemberCreationRequestDto requestDto = MemberCreationRequestDto.builder()
                .name("안유진")
                .build();

        // 동일한 OIDC ID로 다시 회원가입을 시도할 경우, DuplicateMemberException 발생
        assertThatExceptionOfType(DuplicateMemberException.class)
                .isThrownBy(() -> this.defaultMemberService.createMember(requestDto, oidcId));
    }

    @Test
    @DisplayName("여러 스레드가 동시에 접근하더라도 하나의 회원만 가입됨")
    void saveMember_preventDuplicateInMultithreadedEnvironment() throws ExecutionException, InterruptedException {
        final String oidcId = "123";

        Long generatedMemberId = null;

        // 여러 스레드에서 동시에 동일한 OICD ID로 회원가입 시도
        try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
            for (int i = 0; i < 100; i++) {
                Future<Long> taskResult = executorService.submit(() -> {
                    try {
                        MemberCreationRequestDto requestDto = MemberCreationRequestDto.builder()
                                .name("안유진")
                                .build();
                        MemberCreationResponseDto result = this.defaultMemberService.createMember(requestDto, oidcId);
                        return result.generatedMemberId();
                    } catch (DuplicateMemberException e) {
                        assertThat(e.getId()).isEqualTo(oidcId);
                        return null;
                    }
                });
                Long resultMemberId = taskResult.get();
                if (resultMemberId != null) {
                    generatedMemberId = resultMemberId;
                }
            }

            executorService.shutdown();
            boolean timeout = !executorService.awaitTermination(10, TimeUnit.SECONDS);

            log.info("Timeout occurred: {}", timeout);
        }

        // 검증을 위한 EntityManager 생성
        EntityManager em = this.emf.createEntityManager();

        // Transaction 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // 하나의 회원만 생성되었다는 것을 검증
        List<Member> result = em.createQuery("""
                        SELECT m
                        FROM Member m
                        """, Member.class)
                .getResultList();

        // 쿼리 결과가 1개
        assertThat(result).size().isEqualTo(1);

        // 생성된 회원이 0개가 아니라는 것을 검증
        assertThat(generatedMemberId).isNotNull();

        Member member = result.get(0);
        assertThat(member.getMemberId()).isEqualTo(generatedMemberId);

        // Clear
        em.remove(em.find(MemberProfile.class, generatedMemberId));
        em.remove(em.find(Member.class, generatedMemberId));
        tx.commit();
    }
}