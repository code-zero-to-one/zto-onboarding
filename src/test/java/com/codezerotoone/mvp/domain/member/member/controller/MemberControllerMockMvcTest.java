package com.codezerotoone.mvp.domain.member.member.controller;

import com.codezerotoone.mvp.config.DontNeedMemberMockConfig;
import com.codezerotoone.mvp.domain.member.member.dto.MemberCreationResponseDto;
import com.codezerotoone.mvp.domain.member.member.repository.MemberRepository;
import com.codezerotoone.mvp.domain.member.member.service.MemberService;
import com.codezerotoone.mvp.global.config.security.ApiSecurityFilterChainConfig;
import com.codezerotoone.mvp.global.config.security.SecurityBeansConfig;
import com.codezerotoone.mvp.global.security.token.resolver.JsonBearerTokenResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@Import({
        ApiSecurityFilterChainConfig.class,
        SecurityBeansConfig.class,
        DontNeedMemberMockConfig.class
})
@ActiveProfiles("no-auth")
class MemberControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    MemberService memberService;

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

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                        "name": "알파카"
                    }
                    """,
            """
                    {
                        "name": "오지호",
                        "imageExtension": "jpg"
                    }
                    """,
            """
                    {
                        "name": "유혜령",
                        "imageExtension": "PNG"
                    }
                    """,
            """
                    {
                        "name": "이구호",
                        "imageExtension": "jPG"
                    }
                    """
    })
    @DisplayName("소셜 로그인 - 회원가입 필수값을 모두 입력하면 회원가입 성공")
    void createMember_success(String requestBody) throws Exception {
        when(this.memberService.createMember(any(), any()))
                .thenReturn(new MemberCreationResponseDto(3000L, null));

        String accessToken = "{\"id\":\"1234\"}";

        this.mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .content(requestBody))
                .andDo(log()).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "statusCode": 201,
                            "content": {
                                "generatedMemberId": 3000
                            }
                        }
                        """));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // "name" 값이 없음
            """
                    {}
                    """,
            // "name" 값에서 빈 문자열은 허용하지 않음
            """
                    {
                        "name": ""
                    }
                    """,
            // "name"에 한 글자는 허용하지 않음
            """
                    {
                        "name": "김"
                    }
                    """,
            // "name"에 초성만 입력된 것은 허용하지 않음
            """
                    {
                        "name": "고ㄴ"
                    }
                    """,
            // "name"에 모음만 입력된 것은 허용하지 않음
            """
                    {
                        "name": "고ㅣ"
                    }
                    """,
            // "imageExtension"에는 미리 허용된 확장자만 올 수 있음
            """
                    {
                        "name": "안유진",
                        "imageExtension": "jjj"
                    }
                    """
    })
    @DisplayName("회원가입 필수값이 유효하지 않으면 400 Bad Request")
    void createMember_badRequest400_becauseRequiredFieldsNotValid(String requestBody) throws Exception {
        String accessToken = "{\"id\":\"1234\"}";

        this.mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .content(requestBody))
                .andDo(log()).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "statusCode": 400,
                            "errorName": "INVALID_PARAMETER",
                            "errorCode": "CMM001"
                        }
                        """));
    }
}