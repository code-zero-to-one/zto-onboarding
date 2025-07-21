package com.codezerotoone.mvp.global.security;

import com.codezerotoone.mvp.domain.member.auth.constant.AuthorizedHttpMethod;
import com.codezerotoone.mvp.domain.member.auth.dto.RoleDto;
import com.codezerotoone.mvp.domain.member.auth.dto.response.AllowedEndpointForRole;
import com.codezerotoone.mvp.domain.member.auth.entity.Role;
import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.global.security.exception.errorcode.SecurityErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Security Filter Chain with JSON based authentication
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@Slf4j
@ActiveProfiles("test")
public class JsonBasedSecurityFilterTest {

    @LocalServerPort
    int port;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MemberRepository memberRepository;

    @TestConfiguration
    static class JsonBasedSecurityFilterTestConfiguration {

        @Bean
        @Primary
        public MemberRepository mockMemberRepository() {
            return mock(MemberRepository.class);
        }

        @Bean
        @Primary
        public RoleService roleService() {
            RoleService roleService = mock(RoleService.class);

            when(roleService.getAllAccessPermission())
                    .thenReturn(Map.of(
                            AuthorizedHttpMethod.GET, List.of(
                                    new AllowedEndpointForRole("/api/v1/test", List.of(RoleDto.of(Role.getMemberRole())))
                            )
                    ));
            return roleService;
        }
    }

    @Test
    @DisplayName("Authorization 헤더에 JSON 형식 데이터에 loginId를 포함해서 보내면 "
            + "loginId를 추출해서 Authentication 진행")
    void jsonBearerToken() {
        // Mock
        final String loginId = "sample@gmail.com";
        final Long memberId = 1000L;
        Member sampleMember = Member.createGeneralMemberBySocialLogin(
                "name",
                "awehfhighavsdi"
        );
        ReflectionTestUtils.setField(sampleMember, "memberId", memberId);
        when(this.memberRepository.findByOdicId(any()))
                .thenReturn(Optional.of(sampleMember));

        // Request Entity
        RequestEntity<Void> requestEntity = RequestEntity.get("http://localhost:" + port + "/api/v1/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer { \"id\": \"awehfhighavsdi\" }")
                .build();

        assertThatNoException().isThrownBy(() -> {
            ResponseEntity<String> result = this.restTemplate.exchange(requestEntity, String.class);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
            assertThat(result.getBody()).isEqualTo(DummyController.RETURN_VALUE);
        });
    }

    @Test
    @DisplayName("Bearer Token을 Authorization 헤더에 담지 않으면 401")
    void withoutAuthorizationHeader_401() {
        ResponseEntity<String> result =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/v1/test", String.class);
        log.info("result={}", result);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
        JSONObject body = new JSONObject(result.getBody());
        assertThat(body.getInt("statusCode")).isEqualTo(401);
        assertThat(body.getString("errorCode")).isEqualTo(SecurityErrorCode.AUTHENTICATION_FAILED.getErrorCode());
        assertThat(body.getString("errorName")).isEqualTo(SecurityErrorCode.AUTHENTICATION_FAILED.name());
        assertThat(body.getString("message")).isEqualTo(SecurityErrorCode.AUTHENTICATION_FAILED.getMessage());
    }
}
