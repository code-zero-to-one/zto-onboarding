package com.codezerotoone.mvp.domain.member.memberprofile.service;

import com.codezerotoone.mvp.domain.category.techstack.dto.response.TechStackResponse;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStack;
import com.codezerotoone.mvp.domain.category.techstack.entity.TechStackRef;
import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;
import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.image.entity.dto.ResizedImageInfo;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberInfoUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.FullMemberProfileResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberInfoResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberInfoUpdateResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberProfileResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.*;
import com.codezerotoone.mvp.global.file.constant.FileClassification;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.show_sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
@Transactional
@ActiveProfiles("test")
public class MemberProfileServiceIntegrationTest {

    @Autowired
    MemberProfileService memberProfileService;

    @Autowired
    EntityManager em;

    List<AvailableStudyTime> availableStudyTimes;

    List<TechStack> techStacks;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public FileUrlResolver testFileUrlResolver() {
            return new FileUrlResolver() {

                @Override
                public String generateUuidFileUri(String path, ImageExtension extension) throws IllegalArgumentException {
                    return path + "/test-file." + extension.getExtension();
                }

                @Override
                public String generateFileUploadUrl(String fileUri) throws NullPointerException {
                    return "https://img.zeroone.it.kr/" + fileUri;
                }

                @Override
                public String generateFileUploadUrl(String path, ImageExtension extension) throws NullPointerException {
                    return generateFileUploadUrl(generateUuidFileUri(path, extension));
                }

                @Override
                public String getFileLocation(FileClassification fileClassification) {
                    return "https://img.zeroone.it.kr";
                }
            };
        }
    }

    @BeforeEach
    void setUp() {
        // Prerequisite entities
        this.em.persist(Role.getMemberRole());

        // Study subject
        List<StudySubject> studySubjects = List.of(instantiateEntity(StudySubject.class, Map.of(
                "studySubjectId", "CS_DEEP",
                "studySubjectName", "Cs Deep Dive"
        )));

        studySubjects.forEach(this.em::persist);

        // Available Study Times
        this.availableStudyTimes = List.of(
                instantiateEntity(AvailableStudyTime.class, Map.of(
                        "availableStudyTimeId", 1L,
                        "fromTime", LocalTime.of(9, 0),
                        "toTime", LocalTime.of(12, 0),
                        "label", "오전"
                )),
                instantiateEntity(AvailableStudyTime.class, Map.of(
                        "availableStudyTimeId", 2L,
                        "fromTime", LocalTime.of(12, 0),
                        "toTime", LocalTime.of(13, 0),
                        "label", "점심"
                ))
        );

        this.availableStudyTimes.forEach((ast) -> this.em.persist(ast));

        // Tech stacks
        this.techStacks = List.of(
                TechStack.builder()
                        .name("Back-end")
                        .level(1)
                        .build()
        );

        this.techStacks.forEach(this.em::persist);

        // SocialMediaType
        this.em.persist(instantiateEntity(SocialMediaType.class, Map.of(
                "socialMediaTypeId", "GITHUB",
                "socialMediaName", "GitHub"
        )));

        this.em.persist(instantiateEntity(SocialMediaType.class, Map.of(
                "socialMediaTypeId", "BLOG_OR_SNS",
                "socialMediaName", "블로그/SNS"
        )));
    }

    private <T> T instantiateEntity(Class<T> entityClass, Map<String, Object> fieldValues) {
        try {
            Constructor<T> constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            fieldValues.forEach((k, v) -> ReflectionTestUtils.setField(instance, k, v));
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @DisplayName("회원 정보 수정을 여러 번 해도 똑같은 결과")
    @ValueSource(ints = {
            1, 2, 3, 7, 9, 50
    })
    void updateMemberInfo_idempotency(int count) {
        // Given
        Member member = Member.createGeneralMemberBySocialLogin("박신주", "45126314515");
        this.em.persist(member);

        // Parameters
        final String selfIntroduction = "안녕하세요";
        final String studyPlan = "럭키비키";
        final String preferredStudySubjectId = "CS_DEEP";
        final List<Long> availableStudyTimeIds = availableStudyTimes
                .stream()
                .map(AvailableStudyTime::getAvailableStudyTimeId)
                .toList();
        final List<Long> techStackIds = this.techStacks
                .stream()
                .map(TechStack::getTechStackId)
                .toList();

        // DTO
        MemberInfoUpdateRequestDto dto = MemberInfoUpdateRequestDto.builder()
                .selfIntroduction(selfIntroduction)
                .studyPlan(studyPlan)
                .preferredStudySubjectId(preferredStudySubjectId)
                .availableStudyTimeIds(availableStudyTimeIds)
                .techStackIds(techStackIds)
                .build();

        // When
        List<MemberInfoUpdateResponseDto> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(this.memberProfileService.updateMemberInfo(member.getId(), dto, true));
        }

        // Then
        // validation of return value
        // Compare parameters and returns
        {
            MemberInfoUpdateResponseDto firstResult = results.getFirst();
            assertThat(firstResult.memberId()).isEqualTo(member.getId());
            assertThat(firstResult.selfIntroduction()).isEqualTo(selfIntroduction);
            assertThat(firstResult.studyPlan()).isEqualTo(studyPlan);
            assertThat(firstResult.preferredStudySubjectId()).isEqualTo(preferredStudySubjectId);
            assertThat(firstResult.techStackIds()).containsExactlyInAnyOrder(techStackIds.toArray(new Long[0]));
            assertThat(firstResult.availableStudyTimeIds())
                    .containsExactlyInAnyOrder(availableStudyTimeIds.toArray(new Long[0]));
        }

        // All results are the same.
        {
            MemberInfoUpdateResponseDto firstResult = results.getFirst();
            for (MemberInfoUpdateResponseDto result : results) {
                assertThat(result).isEqualTo(firstResult);
            }
        }

        // Validate entity
        MemberProfile memberProfile = this.em.find(MemberProfile.class, member.getId());
        MemberInfo memberInfo = memberProfile.getMemberInfo();
        assertThat(memberInfo.getSelfIntroduction()).isEqualTo(selfIntroduction);
        assertThat(memberInfo.getStudyPlan()).isEqualTo(studyPlan);
        assertThat(memberInfo.getPreferredStudySubject().getStudySubjectId()).isEqualTo(preferredStudySubjectId);
        assertThat(memberInfo.getAvailableStudyTimes().stream().map(AvailableStudyTime::getAvailableStudyTimeId))
                .containsExactlyInAnyOrder(availableStudyTimeIds.toArray(new Long[0]));
        assertThat(memberInfo.getTechStackRefs().stream().map(TechStackRef::getTechStack).map(TechStack::getTechStackId))
                .containsExactlyInAnyOrder(techStackIds.toArray(new Long[0]));
    }

    @Test
    @DisplayName("회원 정보/프로필 업데이트 후 회원 데이터를 가져오면 업데이트된 데이터가 반영돼 있다.")
    void getMemberProfile_success() {
        Member member = Member.createGeneralMemberBySocialLogin("도규태", "5136542");
        this.em.persist(member);

        // Parameters
        // Member Profile Data
        final String name = "유진수";
        final String tel = "010-4524-5252";
        final String githubLink = "https://github.com/rudeh1253";
        final Mbti mbti = Mbti.ISTP;
        final List<String> interests = List.of("요가", "여행");
        final ImageExtension profileImageExtension = ImageExtension.JPG;

        // Member Info
        final String selfIntroduction = "Hello, World!";
        final String preferredStudySubjectId = "CS_DEEP";
        final List<Long> availableStudyTimeIds = List.of(1L, 2L);
        final List<Long> techStackIds = List.of(1L);

        // Update member profile
        // Iterate multiple times
        for (int i = 0; i < 100; i++) {
            this.memberProfileService.updateProfile(member.getId(), MemberProfileUpdateRequestDto.builder()
                    .name("유진수")
                    .tel("010-4124-2422")
                    .githubLink("https://github.com/rudeh1253")
                    .name(name)
                    .tel(tel)
                    .githubLink(githubLink)
                    .mbti(mbti)
                    .interests(interests)
                    .profileImageExtension(profileImageExtension)
                    .build());

            this.memberProfileService.updateMemberInfo(member.getId(), MemberInfoUpdateRequestDto.builder()
                    .selfIntroduction(selfIntroduction)
                    .preferredStudySubjectId(preferredStudySubjectId)
                    .availableStudyTimeIds(availableStudyTimeIds)
                    .techStackIds(techStackIds)
                    .build());
        }

        Image profileImage = Image.create("https://img.zeroone.it.kr",
                new ResizedImageInfo("sample.png", ImageSizeType.ORIGINAL));
        this.em.persist(profileImage);

        // For Image url test
        member.getMemberProfile().getMemberProfileData().updateProfileImage(profileImage);

        // Get Member Profile
        FullMemberProfileResponseDto memberProfileDto = this.memberProfileService.getMemberProfile(member.getId());
        MemberProfileResponseDto memberProfileDataDto = memberProfileDto.memberProfile();
        MemberInfoResponseDto memberInfoDto = memberProfileDto.memberInfo();

        assertThat(memberProfileDto.autoMatching()).isFalse(); // autoMatching is false by default
        assertThat(memberProfileDataDto.memberName()).isEqualTo(name);
        assertThat(memberProfileDataDto.tel()).isEqualTo(tel);
        assertThat(memberProfileDataDto.githubLink().url()).isEqualTo(githubLink);
        assertThat(memberProfileDataDto.mbti()).isEqualTo(mbti);
//        assertThat(memberProfileDataDto.interests()).extracting("name").containsExactlyInAnyOrder(interests);
        assertThat(memberProfileDataDto.profileImage().getResizedImages().getFirst().getResizedImageUrl())
                .startsWith("https://img.zeroone.it.kr/");
        assertThat(memberInfoDto.selfIntroduction()).isEqualTo(selfIntroduction);
        assertThat(memberInfoDto.preferredStudySubject().getStudySubjectId()).isEqualTo(preferredStudySubjectId);
        assertThat(memberInfoDto.availableStudyTimes()).extracting("id")
                .containsExactlyInAnyOrder(availableStudyTimeIds.toArray(new Long[0]));
        assertThat(memberInfoDto.techStacks()).extracting("techStackId")
                .containsExactlyInAnyOrder(techStackIds.toArray(new Long[0]));

    }

    @Test
    @DisplayName("기술스택을 여러 번 반복해서 업데이트하더라도 업데이트 전 기술스택이 남아있지 않고 업데이트된 기술스택만 남아 있다.")
    void updateMemberInfo_techStackDuplication() {
        // Given
        final String memberName = "이유민";

        Member member = Member.createGeneralMemberBySocialLogin(memberName, "152621421");
        this.em.persist(member);
        Long memberId = member.getId();

        List<Long> techStackIdsToUpdate = this.techStacks.stream()
                .map(TechStack::getTechStackId)
                .toList();

        MemberInfoUpdateRequestDto updateMemberInfoDto = MemberInfoUpdateRequestDto.builder()
                .techStackIds(techStackIdsToUpdate)
                .build();

        // 테스트를 {iterationCount}만큼 반복
        final int iterationCount = 100;
        for (int i = 0; i < iterationCount; i++) {
            // When
            MemberInfoUpdateResponseDto result =
                    this.memberProfileService.updateMemberInfo(memberId, updateMemberInfoDto, true);

            // Then
            // 업데이트 메소드 결과 검증
            assertThat(result.memberId()).isEqualTo(memberId);
            assertThat(result.techStackIds()).containsExactlyInAnyOrder(techStackIdsToUpdate.toArray(new Long[0]));

            // Storage에 저장된 데이터 검증
            FullMemberProfileResponseDto memberProfile = this.memberProfileService.getMemberProfile(memberId);
            assertThat(memberProfile.memberId()).isEqualTo(memberId);
            assertThat(memberProfile.memberProfile().memberName()).isEqualTo(memberName);

            // 회원의 기술스택 검증
            List<Long> techStackIdsOfMember = memberProfile.memberInfo()
                    .techStacks()
                    .stream()
                    .map(TechStackResponse::techStackId)
                    .toList();
            assertThat(techStackIdsOfMember).containsExactlyInAnyOrder(techStackIdsToUpdate.toArray(new Long[0]));
        }
    }
}
