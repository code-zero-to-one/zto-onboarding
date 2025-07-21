package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import com.codezerotoone.mvp.global.api.error.docs.child.TestChildErrorCode;
import com.codezerotoone.mvp.global.api.error.docs.child.TestDocumentedErrorCode;
import com.codezerotoone.mvp.global.api.error.docs.child.TestNamedErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ErrorCodeSpecScannerTest {

    @Test
    @DisplayName("basePackage 이하의 ErrorCodeSpec 구현체 클래스를 모두 탐색")
    void testScan() {
        Collection<? extends Class<? extends ErrorCodeSpec>> result =
                ErrorCodeSpecScanner.findAllErrorCodeSpecClasses(getClass().getPackageName());
        log.info("result:\n{}",
                result.stream()
                        .reduce("", (s, e) -> e + "\n" + s, (e1, e2) -> e1 + "\n" + e2));
        assertThat(result).size().isEqualTo(4);
        assertThat(result.stream().allMatch((c) -> c == TestErrorCode.class
                || c == TestChildErrorCode.class
                || c == TestDocumentedErrorCode.class
                || c == TestNamedErrorCode.class)).isTrue();
    }
}