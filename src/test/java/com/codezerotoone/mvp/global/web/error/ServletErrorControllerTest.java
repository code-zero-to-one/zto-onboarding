package com.codezerotoone.mvp.global.web.error;

import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServletErrorControllerTest {
    private final ServletErrorController servletErrorController = new ServletErrorController();

    @DisplayName("HTTP Servlet 요청 객체를 입력하면 적합한 예외 응답 객체를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "404", "405", "415", "500", "503"
    })
    void handleError(int statusCode) {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(statusCode);

        // when
        ResponseEntity<ErrorResponse> response = servletErrorController.handleError(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
    }
}
