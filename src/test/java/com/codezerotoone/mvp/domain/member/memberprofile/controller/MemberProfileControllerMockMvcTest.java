package com.codezerotoone.mvp.domain.member.memberprofile.controller;

import com.codezerotoone.mvp.domain.member.auth.service.RoleService;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.IdNameDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberProfileUpdateResponseDto;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.NullArgumentException;
import com.codezerotoone.mvp.domain.member.memberprofile.service.MemberProfileService;
import com.codezerotoone.mvp.global.config.security.ApiSecurityFilterChainConfig;
import com.codezerotoone.mvp.global.config.security.SecurityBeansConfig;
import com.codezerotoone.mvp.global.security.token.resolver.JsonBearerTokenResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberProfileController.class)
@Import({
        ApiSecurityFilterChainConfig.class,
        SecurityBeansConfig.class
})
@ActiveProfiles("no-auth")
public class MemberProfileControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    RoleService roleService;

    @MockitoBean
    MemberProfileService memberProfileService;

    @MockitoBean
    MemberRepository memberRepository;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public BearerTokenResolver jsonBearerTokenResolver() {
            return new JsonBearerTokenResolver();
        }
    }

    @BeforeEach
    void beforeEach() {
        when(this.memberRepository.findByLoginId(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - 성공")
    void test_updateMemberProfile_success() throws Exception {
        // Given
        when(this.memberProfileService.updateProfile(eq(10000L), any(MemberProfileUpdateRequestDto.class), eq(false)))
                .thenReturn(MemberProfileUpdateResponseDto.builder()
                        .memberId(10000L)
                        .name("홍진호")
                        .profileImageUploadUrl(null)
                        .tel("010-1234-1234")
                        .githubLink("https://github.com/rudeh1253")
                        .blogOrSnsLink("https://velog.io/@rudeh1253")
                        .simpleIntroduction("백엔드 개발자입니다.")
                        .mbti(Mbti.ENTP)
                        .interests(List.of(
                                new IdNameDto(1L, "Java 24"),
                                new IdNameDto(2L, "AWS Certification")
                        ))
                        .build());

        // When
        this.mockMvc.perform(patch("/api/v1/members/{memberId}/profile", 10000L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "홍진호",
                            "tel": "010-1234-1234",
                            "githubLink": "https://github.com/rudeh1253",
                            "blogOrSnsLink": "https://velog.io/@rudeh1253",
                            "mbti": "ENTP",
                            "interests": [
                                "Java 24", "AWS Certification"
                            ],
                            "profileImageExtension": null
                        }
                        """))
                .andDo(print()).andDo(log())

                // Then
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "statusCode": 200,
                          "content": {
                            "memberId": 10000,
                            "name": "홍진호",
                            "profileImageUploadUrl": null,
                            "tel": "010-1234-1234",
                            "githubLink": "https://github.com/rudeh1253",
                            "blogOrSnsLink": "https://velog.io/@rudeh1253",
                            "simpleIntroduction": "백엔드 개발자입니다.",
                            "mbti": "ENTP",
                            "interests": [
                              {
                                "id": 1,
                                "name": "Java 24"
                              },
                              {
                                "id": 2,
                                "name": "AWS Certification"
                              }
                            ]
                          }
                        }
                        """));
    }

    @Test
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - 회원 수정 시 \"ignore-null\"이 false일 경우 \"name\"이 null이면 400 응답이 반환된다.")
    void updateMemberProfile_400_nameIsNull() throws Exception {
        when(this.memberProfileService.updateProfile(anyLong(), any(), anyBoolean()))
                .thenThrow(new NullArgumentException("name", "\"name\" should not be null"));

        this.mockMvc.perform(patch("/api/v1/members/{memberId}/profile", 10000L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "tel": "010-1234-1234"
                        }
                        """))
                .andDo(print()).andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 400,
                            "errorCode": "CMM001",
                            "errorName": "INVALID_PARAMETER",
                            "detail": [
                                {
                                    "paramName": "name"
                                }
                            ]
                        }
                        """));
    }

    @Test
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - 회원 수정 시 \"ignore-null\"이 false일 경우 \"tel\"이 null이면 400 응답이 반환된다.")
    void updateMemberProfile_400_telIsNull() throws Exception {
        when(this.memberProfileService.updateProfile(anyLong(), any(), anyBoolean()))
                .thenThrow(new NullArgumentException("tel", "\"tel\" should not be null"));

        this.mockMvc.perform(patch("/api/v1/members/{memberId}/profile", 10000L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "이상혁"
                        }
                        """))
                .andDo(print()).andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 400,
                            "errorCode": "CMM001",
                            "errorName": "INVALID_PARAMETER",
                            "detail": [
                                {
                                    "paramName": "tel"
                                }
                            ]
                        }
                        """));
    }

    @ParameterizedTest
    @DisplayName("PATCH /api/v1/members/{memberId}/profile - \"tel\"이 ^\\d{1,3}-\\d{3,4}-\\d{4}$ 형식이 아니면 400 에러가 반한된다.")
    @ValueSource(strings = {
            "0114-5251-2424",
            "010-1234",
            "011-32-1562",
            "015-2262-53513",
            "010",
            "2626-7373",
            "01012341234"
    })
    void updateMemberProfile_telDoesNotMatchPattern(String tel) throws Exception {
        this.mockMvc.perform(patch("/api/v1/members/{memberId}/profile", 10000L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                        {
                            "name": "이상혁",
                            "tel": "%s"
                        }
                        """, tel)))
                .andDo(print()).andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 400,
                            "errorCode": "CMM001",
                            "errorName": "INVALID_PARAMETER",
                            "detail": [
                                {
                                    "paramName": "tel"
                                }
                            ]
                        }
                        """));
    }
}
