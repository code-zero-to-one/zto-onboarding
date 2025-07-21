package com.codezerotoone.mvp.global.file.uploader;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * <p>로컬에 파일을 저장하는 LocalFileUploader</p>
 *
 * @author PGD
 */
@Slf4j
public class LocalFileUploader implements FileUploader {
    private static final int BUFFER_SIZE = 1024; // TODO: 외부 설정으로 관리할 것인가

    private final String directoryRoot;

    public LocalFileUploader(String directoryRoot) {
        this.directoryRoot = directoryRoot.endsWith("/") ? directoryRoot : directoryRoot + "/";
    }

    @Override
    public void upload(InputStream is, String filePath) throws IOException {
        File file = new File(this.directoryRoot + filePath);
        log.info("file={}", file);
        if (file.exists()) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        int fileSize = is.available();
        int readSize = 0;
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
                readSize += read;

                // 파일 생성 진행도 체크
                if (log.isTraceEnabled()) {
                    log.trace("{}: {}", filePath, Math.round((double) readSize / fileSize * 100));
                }
            }
            bos.flush();
        }
    }

    @Override
    public void upload(byte[] data, String filePath) throws IOException {
        File file = new File(this.directoryRoot + filePath);
        log.info("file={}", file);
        if (file.exists()) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(data);
            bos.flush();
        }
    }
}
