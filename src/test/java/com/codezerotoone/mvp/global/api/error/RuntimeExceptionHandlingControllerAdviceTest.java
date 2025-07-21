package com.codezerotoone.mvp.global.api.error;

import com.codezerotoone.mvp.config.DontNeedMemberMockConfig;
import com.codezerotoone.mvp.global.security.ErrorTestController;
import com.codezerotoone.mvp.global.security.WebTestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ErrorTestController.class)
@Import({WebTestSecurityConfig.class, DontNeedMemberMockConfig.class})
@ActiveProfiles("no-auth")
class RuntimeExceptionHandlingControllerAdviceTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("ArithmeticException과 같은 핸들링되지 않은 예외가 발생할 경우, 공통 에러 포맷에 맞는 응답")
    void internalServerErrorTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/throw-error"))
                .andDo(log())
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("""
                        {
                            "statusCode": 500,
                            "errorCode": "CMM002",
                            "errorName": "INTERNAL_SERVER_ERROR"
                        }
                        """));
    }
}
