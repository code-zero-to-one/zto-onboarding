package com.codezerotoone.mvp.domain.member.memberprofile.integration;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermission;
import com.codezerotoone.mvp.domain.member.auth.entity.AccessPermissionRole;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.repository.AccessPermissionRepository;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.exception.errorcode.MemberErrorCode;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.*;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberInterest;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfile;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfileData;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.SocialMedia;
import com.codezerotoone.mvp.global.api.format.BaseResponse;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import com.codezerotoone.mvp.global.security.exception.errorcode.SecurityErrorCode;
import com.codezerotoone.mvp.global.util.methodoverride.HttpMethodOverrideConstant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(
        properties = {
                "server.port=15243",
                "server.origin=http://localhost:15243",
                "spring.jpa.properties.hibernate.show_sql=true",
                "spring.jpa.properties.hibernate.format_sql=true",
                "logging.level.com.codezerotoone.mvp=trace"
        }
)
@Import(MemberProfileIntegrationTest.TestConfig.class)
// TODO: 통합 테스트 시 Testcontainers 등을 활용해 운영 서버 혹은 QA 서버와 동일한 환경 세팅
@AutoConfigureTestDatabase
@Slf4j
@ActiveProfiles("test")
public class MemberProfileIntegrationTest {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DataSource dataSource;

    @Autowired
    EntityManager em;

    @TestConfiguration
    static class TestConfig {

        // 스프링 컨텍스트 초기화 전에 Role과 AccessPermission, AccessPermissionRole 데이터를 넣어 놓으려고
        // 하는데, 여기서 데이터를 넣으니까 AfterEach에서 데이터가 초기화돼서 다른 테스트에 영향을 줌
        // 그래서 Mock 객체 생성
        @Bean
        @Primary
        public AccessPermissionRepository mockAccessPermissionRepository() throws Exception {
            AccessPermissionRepository mockRepo = mock(AccessPermissionRepository.class);

            // Prepare mock data
            List<AccessPermission> mockPermissions = new ArrayList<>();

            // Roles (simple mock roles)
            Role roleMember = Role.getMemberRole();
            Role roleGuest = Role.getGuestRole();

            // Access permissions with their roles
            mockPermissions.add(createPermissionWithRoles(1L, "/api/v1/me", AuthorizedHttpMethod.GET, List.of(
                    createAccessPermissionRole(1L, roleMember)
            )));
            mockPermissions.add(createPermissionWithRoles(2L, "/api/v1/members", AuthorizedHttpMethod.POST, List.of(
                    createAccessPermissionRole(2L, roleGuest)
            )));
            mockPermissions.add(createPermissionWithRoles(3L, "/api/v1/members", AuthorizedHttpMethod.DELETE, List.of(
                    createAccessPermissionRole(3L, roleMember)
            )));
            mockPermissions.add(createPermissionWithRoles(4L, "/api/v1/members/*/profile", AuthorizedHttpMethod.PATCH, List.of(
                    createAccessPermissionRole(4L, roleMember)
            )));
            mockPermissions.add(createPermissionWithRoles(5L, "/api/v1/members/*/profile/for-study", AuthorizedHttpMethod.GET, List.of(
                    createAccessPermissionRole(5L, roleMember)
            )));
            mockPermissions.add(createPermissionWithRoles(6L, "/api/v1/members/*/profile/info", AuthorizedHttpMethod.PATCH, List.of(
                    createAccessPermissionRole(6L, roleMember)
            )));

            when(mockRepo.findAll()).thenReturn(mockPermissions);

            return mockRepo;
        }

        private AccessPermission createPermissionWithRoles(Long id,
                                                           String endpoint,
                                                           AuthorizedHttpMethod method,
                                                           List<AccessPermissionRole> roles) throws Exception {
            Constructor<AccessPermission> constructor = AccessPermission.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            AccessPermission permission = constructor.newInstance();
            setField(permission, "permissionId", id);
            setField(permission, "endpoint", endpoint);
            setField(permission, "httpMethod", method);

            // Link roles to permission
            for (AccessPermissionRole roleLink : roles) {
                setField(roleLink, "accessPermission", permission);
            }

            setField(permission, "accessPermissionRole", roles);
            return permission;
        }

        private AccessPermissionRole createAccessPermissionRole(Long id, Role role)
                throws Exception {
            Constructor<AccessPermissionRole> constructor = AccessPermissionRole.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            AccessPermissionRole accessPermissionRole = constructor.newInstance();
            setField(accessPermissionRole, "accessPermissionRoleId", id);
            setField(accessPermissionRole, "role", role);
            return accessPermissionRole;
        }

        // Utility method for reflection field injection
        private void setField(Object target, String fieldName, Object value) {
            try {
                var field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set field via reflection", e);
            }
        }
    }

    @BeforeEach
    void insertBaseRecords() {
        // TODO: 이런 boilerplate 코드 어딘가에 빼놓으면 좋을지도
        String setUpSql = """
                /* 권한 목록 */
                INSERT INTO role (role_id, role_name) VALUES ('ROLE_MEMBER', 'Member');
                INSERT INTO role (role_id, role_name) VALUES ('ROLE_ADMIN', 'Administrator');
                INSERT INTO role (role_id, role_name) VALUES ('ROLE_GUEST', 'Guest');

                /* 가능시간대 */
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (1, '09:00', '12:00', '오전');
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (2, '12:00', '13:00', '점심');
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (3, '13:00', '18:00', '오후');
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (4, '18:00', '21:00', '저녁');
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (5, '21:00', '23:00', '심야');
                INSERT INTO available_study_time (available_study_time_id, from_time, to_time, label) VALUES (6, NULL, NULL, '시간 협의 가능');

                /* (선호하는) 스터디 주제 */
                INSERT INTO study_subject (study_subject_id, study_subject_name) VALUES ('ALL', '전체');
                INSERT INTO study_subject (study_subject_id, study_subject_name) VALUES ('FRONTEND_DEEP', 'Front-end Deep Dive');
                INSERT INTO study_subject (study_subject_id, study_subject_name) VALUES ('BACKEND_DEEP', 'Back-end Deep Dive');
                INSERT INTO study_subject (study_subject_id, study_subject_name) VALUES ('CS_DEEP', 'CS Deep Dive');

                /* 소셜 미디어 타입 */
                INSERT INTO social_media_type (social_media_type_id, social_media_name, icon_id) VALUES ('GITHUB', 'GitHub', NULL);
                INSERT INTO social_media_type (social_media_type_id, social_media_name, icon_id) VALUES ('BLOG_OR_SNS', '블로그/SNS', NULL);
                """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(setUpSql);
            stmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to insert base records before test");
        } finally {
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(conn);
        }
    }

    @AfterEach
    void tearDown() {
        // GPT가 tearDown 코드 짜 줬어요
        // 고마워요 GPT!
        String tearDownSql = """
                DELETE FROM social_media;
                DELETE FROM member_interest;
                DELETE FROM profile_avl_time;
                DELETE FROM member_profile;
                DELETE FROM member;
                DELETE FROM access_permission_role;
                DELETE FROM access_permission;
                DELETE FROM social_media_type;
                DELETE FROM study_subject;
                DELETE FROM available_study_time;
                DELETE FROM role;
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dataSource.getConnection();
            stmt = conn.prepareStatement(tearDownSql);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(conn);
        }
    }

    // TestPropertySource에서 정의된 server.origin 값
    @Value("${server.origin}")
    String serverOrigin;

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile - 존재하지 않는 회원일 경우 404")
    void findMemberProfile_404() throws Exception {
        final Long exampleMemberId = 10000L;
        RequestEntity<Void> request = RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile", exampleMemberId)
                .build();

        // For measuring timestamp
        LocalDateTime beforeRequest = LocalDateTime.now();

        // When
        ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);

        log.info("response={}", response);

        // Check status code
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))).isTrue();

        // Check response body
        ErrorResponse resultBody = this.objectMapper.readValue(response.getBody(), ErrorResponse.class);
        assertThat(resultBody.timestamp()).isAfterOrEqualTo(beforeRequest);
        assertThat(resultBody.errorName()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.name());
        assertThat(resultBody.detail()).isEqualTo(Map.of("memberId", exampleMemberId.intValue()));
        assertThat(resultBody.message()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThat(resultBody.errorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getErrorCode());
        assertThat(resultBody.statusCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getStatusCode());
    }

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile - 회원가입 직후 회원 프로필 조회 시 회원 이름만 입력된 상태")
    void findMemberProfile_success() throws Exception {
        // Given
        Member member = Member.createGeneralMemberBySocialLogin("안유진", "123");
        Member savedMember = this.memberRepository.save(member);

        RequestEntity<Void> request = RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile", savedMember.getId())
                .build();

        // When
        ResponseEntity<String> response =
                this.restTemplate.exchange(request, String.class);

        BaseResponse<FullMemberProfileResponseDto> responseBody =
                this.objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });

        log.info("response.statusCode={}", response.getStatusCode());
        log.info("response.body:\n{}", responseBody);

        // Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))).isTrue();

        FullMemberProfileResponseDto content = responseBody.getContent();

        assertThat(content.memberId()).isEqualTo(savedMember.getId());
        assertThat(content.autoMatching()).isFalse(); // autoMatching is false by default

        // Validate content.memberInfo
        MemberInfoResponseDto memberInfo = content.memberInfo();
        assertThat(memberInfo.selfIntroduction()).isNull();
        assertThat(memberInfo.studyPlan()).isNull();
        assertThat(memberInfo.preferredStudySubject()).isNull();
        assertThat(memberInfo.availableStudyTimes()).isEmpty(); // Collection is only empty list
        assertThat(memberInfo.techStacks()).isEmpty();

        // Validate content.memberProfile
        MemberProfileResponseDto memberProfile = content.memberProfile();
        assertThat(memberProfile.memberName()).isEqualTo("안유진"); // Only name is not null
        assertThat(memberProfile.profileImage()).isNull();
        assertThat(memberProfile.simpleIntroduction()).isNull();
        assertThat(memberProfile.mbti()).isNull();
        assertThat(memberProfile.interests()).isEmpty();
        assertThat(memberProfile.githubLink()).isNull();
        assertThat(memberProfile.birthDate()).isNull();
        assertThat(memberProfile.blogOrSnsLink()).isNull();
        assertThat(memberProfile.tel()).isNull();
    }

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile/for-study - 로그인하지 않은 회원은 접근 불가능")
    void findMemberProfileForStudy_401_anonymous() throws Exception {
        // Given
        Member member = Member.createGeneralMemberBySocialLogin("안유진", "123");
        Member savedMember = this.memberRepository.save(member);

        RequestEntity<Void> request = RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile/for-study", savedMember.getId())
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.exchange(request, ErrorResponse.class);
        log.info("response={}", response);

        // Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(401))).isTrue();

        // Body check
        ErrorResponse errorResponse = response.getBody();
        assert errorResponse != null;
        assertThat(errorResponse.errorCode()).isEqualTo(SecurityErrorCode.AUTHENTICATION_FAILED.getErrorCode());
    }

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile/for-study - 게스트는 접근 불가능")
    void findMemberProfileForStudy_403_guest() throws Exception {
        // Given
        Member member = Member.createGeneralMemberBySocialLogin("안유진", "123");
        Member savedMember = this.memberRepository.save(member);

        final String accessToken = "{\"id\":\"456\"}";

        RequestEntity<Void> request = RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile/for-study", savedMember.getId())
                // 로그인은 했지만 회원가입은 하지 않은 경우, Guest
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.exchange(request, ErrorResponse.class);
        log.info("response={}", response);

        // Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403))).isTrue();

        ErrorResponse errorResponse = response.getBody();
        assert errorResponse != null;
        assertThat(errorResponse.errorCode()).isEqualTo(SecurityErrorCode.AUTHORIZATION_FAILED.getErrorCode());
    }

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile/for-study - 존재하지 않은 회원일 경우 404")
    void findMemberProfileForStudy_404() throws Exception {
        // Given
        Member requester = Member.createGeneralMemberBySocialLogin("안유진", "123");
        this.memberRepository.save(requester);

        final String accessToken = "{\"id\":\"123\"}";

        RequestEntity<Void> request = RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile/for-study", 5000)
                // 로그인은 했지만 회원가입은 하지 않은 경우, Guest
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        // For timestamp check
        LocalDateTime beforeRequest = LocalDateTime.now();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.exchange(request, ErrorResponse.class);
        log.info("response={}", response);

        // Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))).isTrue();

        ErrorResponse resultBody = response.getBody();
        assert resultBody != null;
        assertThat(resultBody.timestamp()).isAfterOrEqualTo(beforeRequest);
        assertThat(resultBody.errorName()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.name());
        assertThat(resultBody.detail()).isEqualTo(Map.of("memberId", 5000));
        assertThat(resultBody.message()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThat(resultBody.errorCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getErrorCode());
        assertThat(resultBody.statusCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("GET /api/v1/members/{memberId}/profile/for-study - 가입만 하고 프로필 정보를 입력하지 않은 회원에 대해선 memberId만 가짐")
    void findMemberProfileForStudy_minimalData() throws Exception {
        // Given
        String jsonAccessToken = "{\"id\":\"123\"}";
        Long generatedMemberId;
        RequestEntity<Void> profileGetRequest;
        {
            // 회원가입 진행
            RequestEntity<String> request = RequestEntity.post(this.serverOrigin + "/api/v1/members")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jsonAccessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "name": "안유진"
                            }
                            """);

            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            log.info("body: \n{}", response.getBody());
            BaseResponse<MemberCreationResponseDto> responseBody = this.objectMapper.readValue(response.getBody(),
                    new TypeReference<>() {
                    });

            generatedMemberId = responseBody.getContent().generatedMemberId();

            // 스터디에 필요한 회원 프로필 데이터를 가져오기 위한 RequestEntity
            profileGetRequest =
                    RequestEntity.get(this.serverOrigin + "/api/v1/members/{memberId}/profile/for-study",
                                    generatedMemberId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jsonAccessToken)
                            .build();
        }

        // When
        MemberProfileForStudyDto responseContent;
        {
            ResponseEntity<String> response = this.restTemplate.exchange(profileGetRequest, String.class);
            responseContent = this.objectMapper.readValue(response.getBody(),
                    new TypeReference<BaseResponse<MemberProfileForStudyDto>>() {
                    }).getContent();
        }

        // Then
        {
            // 각 필드값을 순회하면서 검증
            for (Field field : MemberProfileForStudyDto.class.getDeclaredFields()) {
                // Field 접근 가능하게 설정
                field.setAccessible(true);

                // 필드값 가져오기
                Object fieldValue = field.get(responseContent);

                // memberId은 존재함
                if ("memberId".equals(field.getName())) {
                    assertThat(fieldValue).isEqualTo(generatedMemberId);
                    continue;
                }

                // Collection은 빈 리스트
                if (field.getType() == List.class) {
                    assertThat((List<Long>) fieldValue).isEmpty();
                    continue;
                }

                // 그외엔 null
                assertThat(fieldValue).isNull();
            }
        }
    }

    @Test
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - JSON property에 오타가 없을 경우 정상적으로 회원 프로필이 업데이트됨")
    void updateMemberProfile_ignoreNullFalse() throws Exception {
        // Given
        final String oicdId = "123";
        final String jsonAccessToken = String.format("{\"id\":\"%s\"}", oicdId);
        Member member = Member.createGeneralMemberBySocialLogin("안유진", oicdId);
        member = this.memberRepository.save(member);

        RequestEntity<String> request = RequestEntity.post(this.serverOrigin + "/api/v1/members/{memberId}/profile",
                        member.getId())
                .header(HttpHeaders.AUTHORIZATION, jsonAccessToken)
                .header(HttpMethodOverrideConstant.HEADER_NAME, "PATCH")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "name": "이현서",
                            "tel": "010-1234-1234",
                            "githubLink": "https://localhost:8080/github",
                            "blogOrSnsLink": "https://localhost:8080/blog",
                            "simpleIntroduction": "백엔드 디벨로퍼입니다.",
                            "mbti": "INFJ",
                            "interests": [
                                "Spring Cloud",
                                "Spring Batch",
                                "Apache Kafka"
                            ]
                        }
                        """);

        // When
        ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
        MemberProfileUpdateResponseDto responseContent = this.objectMapper.readValue(response.getBody(),
                new TypeReference<BaseResponse<MemberProfileUpdateResponseDto>>() {
                }).getContent();

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseContent.memberId()).isEqualTo(member.getId());
        assertThat(responseContent.name()).isEqualTo("이현서");
        assertThat(responseContent.tel()).isEqualTo("010-1234-1234");
        assertThat(responseContent.githubLink()).isEqualTo("https://localhost:8080/github");
        assertThat(responseContent.blogOrSnsLink()).isEqualTo("https://localhost:8080/blog");
        assertThat(responseContent.simpleIntroduction()).isEqualTo("백엔드 디벨로퍼입니다.");
        assertThat(responseContent.mbti()).isEqualTo(Mbti.INFJ);
        assertThat(responseContent.interests()).extracting("name")
                .containsExactlyInAnyOrder(
                        "Spring Cloud",
                        "Spring Batch",
                        "Apache Kafka"
                );

        // Validate member profile in repository
        Member findMember = this.memberRepository.findById(member.getId()).orElseThrow();
        MemberProfile memberProfile = findMember.getMemberProfile();
        MemberProfileData memberProfileData = memberProfile.getMemberProfileData();
        assertThat(memberProfile.getMemberName()).isEqualTo("이현서");
        assertThat(memberProfileData.getTel()).isEqualTo("010-1234-1234");
        assertThat(memberProfileData.getSimpleIntroduction()).isEqualTo("백엔드 디벨로퍼입니다.");
        assertThat(memberProfileData.getMbti()).isEqualTo(Mbti.INFJ);

        // Validate collections
        // GitHub
        SocialMedia github = this.em.createQuery("""
                        SELECT sm
                        FROM SocialMedia sm
                        INNER JOIN SocialMediaType smt ON smt.socialMediaTypeId = sm.socialMediaType.socialMediaTypeId
                        WHERE sm.memberProfile.memberId = :memberId
                            AND smt.socialMediaTypeId = :socialMediaTypeId
                        """, SocialMedia.class)
                .setParameter("memberId", member.getId())
                .setParameter("socialMediaTypeId", PrimarySocialMediaType.GITHUB.name())
                .getSingleResult();
        assertThat(github.getUrl()).isEqualTo("https://localhost:8080/github");

        // Blog or SNS
        SocialMedia blogOrSns = this.em.createQuery("""
                        SELECT sm
                        FROM SocialMedia sm
                        INNER JOIN SocialMediaType smt ON smt.socialMediaTypeId = sm.socialMediaType.socialMediaTypeId
                        WHERE sm.memberProfile.memberId = :memberId
                            AND smt.socialMediaTypeId = :socialMediaTypeId
                        """, SocialMedia.class)
                .setParameter("memberId", member.getId())
                .setParameter("socialMediaTypeId", PrimarySocialMediaType.BLOG_OR_SNS.name())
                .getSingleResult();
        assertThat(blogOrSns.getUrl()).isEqualTo("https://localhost:8080/blog");

        // Interest
        List<MemberInterest> memberInterests = this.em.createQuery("""
                        SELECT mi
                        FROM MemberInterest mi
                        WHERE mi.memberProfile.memberId = :memberId
                        """, MemberInterest.class)
                .setParameter("memberId", member.getId())
                .getResultList();
        assertThat(memberInterests).extracting("name")
                .containsExactlyInAnyOrder(
                        "Spring Cloud",
                        "Spring Batch",
                        "Apache Kafka"
                );
    }

    @Test
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - 똑같은 데이터를 가지고 여러 번 요청을 날려도 결과는 똑같음")
    void updateMemberProfile_idempotency() throws Exception {
        // Given
        final String oicdId = "123";
        final String jsonAccessToken = String.format("{\"id\":\"%s\"}", oicdId);
        Member member = Member.createGeneralMemberBySocialLogin("안유진", oicdId);
        member = this.memberRepository.save(member);

        RequestEntity<String> request = RequestEntity.post(this.serverOrigin + "/api/v1/members/{memberId}/profile",
                        member.getId())
                .header(HttpHeaders.AUTHORIZATION, jsonAccessToken)
                .header(HttpMethodOverrideConstant.HEADER_NAME, "PATCH")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "name": "이현서",
                            "tel": "010-1234-1234",
                            "githubLink": "https://localhost:8080/github",
                            "blogOrSnsLink": "https://localhost:8080/blog",
                            "simpleIntroduction": "백엔드 디벨로퍼입니다.",
                            "mbti": "INFJ",
                            "interests": [
                                "Spring Cloud",
                                "Spring Batch",
                                "Apache Kafka"
                            ]
                        }
                        """);

        // When
        final int COUNT = 10;
        for (int i = 0; i < COUNT; i++) {
            ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
            MemberProfileUpdateResponseDto responseContent = this.objectMapper.readValue(response.getBody(),
                    new TypeReference<BaseResponse<MemberProfileUpdateResponseDto>>() {
                    }).getContent();

            // Then
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(responseContent.memberId()).isEqualTo(member.getId());
            assertThat(responseContent.name()).isEqualTo("이현서");
            assertThat(responseContent.tel()).isEqualTo("010-1234-1234");
            assertThat(responseContent.githubLink()).isEqualTo("https://localhost:8080/github");
            assertThat(responseContent.blogOrSnsLink()).isEqualTo("https://localhost:8080/blog");
            assertThat(responseContent.simpleIntroduction()).isEqualTo("백엔드 디벨로퍼입니다.");
            assertThat(responseContent.mbti()).isEqualTo(Mbti.INFJ);
            assertThat(responseContent.interests()).extracting("name")
                    .containsExactlyInAnyOrder(
                            "Spring Cloud",
                            "Spring Batch",
                            "Apache Kafka"
                    );

            // Validate member profile in repository
            Member findMember = this.memberRepository.findById(member.getId()).orElseThrow();
            MemberProfile memberProfile = findMember.getMemberProfile();
            MemberProfileData memberProfileData = memberProfile.getMemberProfileData();
            assertThat(memberProfile.getMemberName()).isEqualTo("이현서");
            assertThat(memberProfileData.getTel()).isEqualTo("010-1234-1234");
            assertThat(memberProfileData.getSimpleIntroduction()).isEqualTo("백엔드 디벨로퍼입니다.");
            assertThat(memberProfileData.getMbti()).isEqualTo(Mbti.INFJ);

            // Validate collections
            // GitHub
            SocialMedia github = this.em.createQuery("""
                            SELECT sm
                            FROM SocialMedia sm
                            INNER JOIN SocialMediaType smt ON smt.socialMediaTypeId = sm.socialMediaType.socialMediaTypeId
                            WHERE sm.memberProfile.memberId = :memberId
                                AND smt.socialMediaTypeId = :socialMediaTypeId
                            """, SocialMedia.class)
                    .setParameter("memberId", member.getId())
                    .setParameter("socialMediaTypeId", PrimarySocialMediaType.GITHUB.name())
                    .getSingleResult();
            assertThat(github.getUrl()).isEqualTo("https://localhost:8080/github");

            // Blog or SNS
            SocialMedia blogOrSns = this.em.createQuery("""
                            SELECT sm
                            FROM SocialMedia sm
                            INNER JOIN SocialMediaType smt ON smt.socialMediaTypeId = sm.socialMediaType.socialMediaTypeId
                            WHERE sm.memberProfile.memberId = :memberId
                                AND smt.socialMediaTypeId = :socialMediaTypeId
                            """, SocialMedia.class)
                    .setParameter("memberId", member.getId())
                    .setParameter("socialMediaTypeId", PrimarySocialMediaType.BLOG_OR_SNS.name())
                    .getSingleResult();
            assertThat(blogOrSns.getUrl()).isEqualTo("https://localhost:8080/blog");

            // Interest
            List<MemberInterest> memberInterests = this.em.createQuery("""
                            SELECT mi
                            FROM MemberInterest mi
                            WHERE mi.memberProfile.memberId = :memberId
                            """, MemberInterest.class)
                    .setParameter("memberId", member.getId())
                    .getResultList();
            assertThat(memberInterests).extracting("name")
                    .containsExactlyInAnyOrder(
                            "Spring Cloud",
                            "Spring Batch",
                            "Apache Kafka"
                    );
        }
    }
}
