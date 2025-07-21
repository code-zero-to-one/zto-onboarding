package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import com.codezerotoone.mvp.global.api.error.docs.child.TestChildErrorCode;
import com.codezerotoone.mvp.global.api.error.docs.child.TestDocumentedErrorCode;
import com.codezerotoone.mvp.global.api.error.docs.child.TestNamedErrorCode;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DynamicErrorResponseGeneratorTest {

    @Test
    @DisplayName("ErrorCodeSpec 혹은 그에 확장된 인터페이스의 구현체를 DocumentedErrorCodeDto로 변환한다.")
    void convertToErrorResponses_success() {
        // Given
        Map<String, Class<? extends ErrorCodeSpec>> errorCodeSpecByName = Map.of(
                TestErrorCode.class.getName(), TestErrorCode.class,
                TestChildErrorCode.class.getName(), TestChildErrorCode.class,
                TestDocumentedErrorCode.class.getName(), TestDocumentedErrorCode.class,
                TestNamedErrorCode.class.getAnnotation(ErrorCodeDocumentation.class).name(), TestNamedErrorCode.class
        );

        // When
        Map<String, ? extends Collection<DocumentedErrorCodeDto>> result =
                DynamicErrorResponseGenerator.convertToErrorResponses(errorCodeSpecByName.values());

        // Then
        assertThat(result.keySet())
                .containsExactlyInAnyOrder(errorCodeSpecByName.keySet().toArray(new String[0]));

        for (Map.Entry<String, ? extends Collection<DocumentedErrorCodeDto>> entry : result.entrySet()) {
            Class<? extends ErrorCodeSpec> errorCodeEnumClass = errorCodeSpecByName.get(entry.getKey());
            log.info("errorCodeEnumClass={}", errorCodeEnumClass);
            Arrays.stream(errorCodeEnumClass.getEnumConstants())
                    .forEach((e) -> {
                        validateForErrorCode(
                                e,
                                entry.getValue().stream()
                                        .filter((dto) -> e.name().equals(dto.getErrorResponse().errorName()))
                                        .findAny()
                                        .orElseThrow()
                        );
                    });
        }
    }

    private void validateForErrorCode(ErrorCodeSpec expected, DocumentedErrorCodeDto result) {
        if (expected instanceof DocumentedErrorCodeSpec) {
            assertThat(((DocumentedErrorCodeSpec) expected).getDescription())
                    .isEqualTo(result.getDescription());
        } else {
            assertThat(result.getDescription()).isNull();
        }

        ErrorResponse expectedErrorResponse = ErrorResponse.of(expected);
        StaticErrorCodeDto resultErrorResponse = result.getErrorResponse();
        assertThat(resultErrorResponse.errorCode()).isEqualTo(expectedErrorResponse.errorCode());
        assertThat(resultErrorResponse.errorName()).isEqualTo(expectedErrorResponse.errorName());
        assertThat(resultErrorResponse.message()).isEqualTo(expectedErrorResponse.message());
        assertThat(resultErrorResponse.statusCode()).isEqualTo(expectedErrorResponse.statusCode());
    }
}