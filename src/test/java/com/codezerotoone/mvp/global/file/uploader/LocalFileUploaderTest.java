package com.codezerotoone.mvp.global.file.uploader;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class LocalFileUploaderTest {
    LocalFileUploader localFileUploader;
    String directoryRoot; // No trailing "/"

    @BeforeEach
    void setUp() {
        this.directoryRoot = ClassLoader.getSystemClassLoader().getResource("image").getPath();
        directoryRoot = directoryRoot.endsWith("/")
                ? directoryRoot.substring(0, directoryRoot.lastIndexOf("/", directoryRoot.length() - 2))
                : directoryRoot.substring(0, directoryRoot.lastIndexOf("/"));
        log.info("directoryRoot={}", directoryRoot);
        directoryRoot = directoryRoot.startsWith("/") ? directoryRoot.substring(1) : directoryRoot;
        this.localFileUploader = new LocalFileUploader(directoryRoot);
    }

    @Test
    @DisplayName("파일 업로드 테스트")
    void testFileUpload() throws IOException {
        // Given
        final String targetFilePath = "image/target.png";

        // When
        ThrowableAssert.ThrowingCallable behaviour = () -> {
            try (InputStream is = getTestFileInputStream()) {
                this.localFileUploader.upload(is, targetFilePath);
            }
        };

        // Then
        assertThatNoException().isThrownBy(behaviour);

        File createdFile = new File(this.directoryRoot + "/" + targetFilePath);
        assertThat(createdFile.exists()).isTrue();

        try (InputStream originalFileInputStream = getTestFileInputStream();
             InputStream createdFileInputStream = new FileInputStream(createdFile)) {
            int originalFileBytes = originalFileInputStream.available();
            int createdFileBytes = createdFileInputStream.available();
            log.info("originalFileBytes={}, createdFileBytes={}", originalFileBytes, createdFileBytes);
            assertThat(createdFileBytes).isEqualTo(originalFileBytes);

            // 생성된 파일이 정확히 똑같은 데이터를 가지고 있는지 체크
            byte[] originalFileBuffer = new byte[1024];
            byte[] createdFileBuffer = new byte[1024];
            while (true) {
                int originalFileRead = originalFileInputStream.read(originalFileBuffer);
                int createdFileRead = createdFileInputStream.read(createdFileBuffer);

                if (originalFileRead == -1 && createdFileRead == -1) {
                    break;
                } else if (originalFileRead != -1 && createdFileRead != -1) {
                    assertThat(originalFileRead).isEqualTo(createdFileRead);
                    for (int i = 0; i < originalFileRead; i++) {
                        assertThat(createdFileBuffer[i]).isEqualTo(originalFileBuffer[i]);
                    }
                } else {
                    fail();
                }
            }
        }

        // Clear
        createdFile.delete();
    }

    @Test
    @DisplayName("디렉토리가 존재하지 않을 경우, 디렉토리를 생성한 후 그 아래에 파일 생성")
    void testFileUpload_mkdirs() throws IOException {
        // Given
        final String targetFilePath = "somewhere/image/file/target.png";

        // When
        ThrowableAssert.ThrowingCallable behaviour = () -> {
            try (InputStream is = getTestFileInputStream()) {
                this.localFileUploader.upload(is, targetFilePath);
            }
        };

        // Then
        assertThatNoException().isThrownBy(behaviour);

        File createdFile = new File(this.directoryRoot + "/" + targetFilePath);
        assertThat(createdFile.exists()).isTrue();

        try (InputStream originalFileInputStream = getTestFileInputStream();
             InputStream createdFileInputStream = new FileInputStream(createdFile)) {
            int originalFileBytes = originalFileInputStream.available();
            int createdFileBytes = createdFileInputStream.available();
            log.info("originalFileBytes={}, createdFileBytes={}", originalFileBytes, createdFileBytes);
            assertThat(createdFileBytes).isEqualTo(originalFileBytes);

            // 생성된 파일이 정확히 똑같은 데이터를 가지고 있는지 체크
            byte[] originalFileBuffer = new byte[1024];
            byte[] createdFileBuffer = new byte[1024];
            while (true) {
                int originalFileRead = originalFileInputStream.read(originalFileBuffer);
                int createdFileRead = createdFileInputStream.read(createdFileBuffer);

                if (originalFileRead == -1 && createdFileRead == -1) {
                    break;
                } else if (originalFileRead != -1 && createdFileRead != -1) {
                    assertThat(originalFileRead).isEqualTo(createdFileRead);
                    for (int i = 0; i < originalFileRead; i++) {
                        assertThat(createdFileBuffer[i]).isEqualTo(originalFileBuffer[i]);
                    }
                } else {
                    fail();
                }
            }
        }

        // Clear
        File fileToDel = createdFile;
        fileToDel.delete();
        fileToDel = fileToDel.getParentFile();
        fileToDel.delete();
        fileToDel = fileToDel.getParentFile();
        fileToDel.delete();
        fileToDel = fileToDel.getParentFile();
        fileToDel.delete();
    }

    private InputStream getTestFileInputStream() {
        return ClassLoader.getSystemClassLoader().getResourceAsStream("image/test-image.png");
    }
}