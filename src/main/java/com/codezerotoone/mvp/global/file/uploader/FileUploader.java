package com.codezerotoone.mvp.global.file.uploader;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>파일 업로더. 전달받은 파일을 업로드합니다.</p>
 * <p>배포 환경에 따라 로컬 파일 시스템에 저장할 수도 있고 AWS S3에 저장할 수도 있습니다.</p>
 *
 * @author PGD
 */
public interface FileUploader {

    /**
     * 파일 업로드. 이미 존재하는 파일일 경우, 새로운 파일로 대체한다.
     * @param is 파일 데이터를 가지고 있는 <code>InputStream</code>
     * @param filePath 파일 Path. <code>/</code>로 시작하면 안 됩니다.
     * @throws IOException 파일 업로드에 실패할 경우
     */
    @Deprecated
    void upload(InputStream is, String filePath) throws IOException;

    /**
     * 파일 업로드. multipart/form-data를 지원합니다.
     *
     * @param multipartFile 파일 데이터를 가지고 있는 객체
     * @param filePath 파일 Path. <code>/</code>로 시작하면 안 됩니다.
     * @throws IOException 파일 업로드에 실패할 경우
     */
    default void upload(MultipartFile multipartFile, String filePath) throws IOException {
        upload(multipartFile.getBytes(), filePath);
    }

    /**
     * 파일 업로드. 이미 존재하는 파일일 경우, 새로운 파일로 대체한다.
     * @param data 파일 데이터를 가지고 있는 <code>byte</code> array
     * @param filePath 파일 Path. <code>/</code>로 시작하면 안 됩니다.
     * @throws IOException 파일 업로드에 실패할 경우
     */
    void upload(byte[] data, String filePath) throws IOException;
}
