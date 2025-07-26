package com.codezerotoone.mvp.global.api.error;

import com.codezerotoone.mvp.global.web.error.ServletErrorController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommonErrorCodeTest {
    private final ServletErrorController servletErrorController = new ServletErrorController();

    @DisplayName("상태 코드 값을 입력하면 해당하는 공통 예외 코드 객체를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "404", "405", "415", "500", "503"
    })
    void get(int statusCode) {
        // given
        ErrorCodeSpec expectedCode = chooseCommonErrorCode(statusCode);

        // when
        ErrorCodeSpec commonErrorCode = CommonErrorCode.get(statusCode);

        // then
        assertThat(commonErrorCode).isEqualTo(expectedCode);
        assertThat(commonErrorCode.hashCode()).isEqualTo(expectedCode.hashCode());
    }

    private ErrorCodeSpec chooseCommonErrorCode(int statusCode) {
        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.getStatusCode() == statusCode) {
                return errorCode;
            }
        }

        return CommonErrorCode.INTERNAL_SERVER_ERROR;
    }
}
