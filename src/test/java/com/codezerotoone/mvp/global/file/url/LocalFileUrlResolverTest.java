package com.codezerotoone.mvp.global.file.url;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LocalFileUrlResolverTest {

    final String serverOrigin = "http://localhost:8080";

    LocalFileUrlResolver resolver = new LocalFileUrlResolver(serverOrigin);

    @Test
    @DisplayName("적절한 포맷의 파일 확장자와 path를 파라미터로 전달할 경우, \"/\"로 시작하지 않는 파일 URI 생성")
    void generateUuidFileUri_success() {
        // Given
        final String extension = "jpg";
        final String path = "files/images";

        // When
        String result = this.resolver.generateUuidFileUri(extension, path);
        log.info("result={}", result);

        // Then
        Pattern expectedPattern = Pattern.compile("^files/images/[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}_\\d+\\.jpg$");

        assertThat(result).matches(expectedPattern);
    }
}