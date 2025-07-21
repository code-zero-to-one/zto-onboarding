package com.codezerotoone.mvp.global.file.url;

import com.codezerotoone.mvp.global.file.constant.FileClassification;

/**
 * <p>Resolve URL of file. 파일이 어디에 저장되는가에 따라 다양한 구현체가 있을 수 있습니다..
 * e.g., 로컬 파일 시스템, AWS S3, NAS, ...
 * <p>구현체는 다음과 같은 일을 수행해야 합니다:
 * <ol>
 *     <li>파일 URI 생성 (without server URL).</li>
 *     <li>파일 저장 URL 생성</li>
 *     <li>파일이 저장된 위치 반환</li>
 * </ol>
 *
 * @author PGD
 */
public interface FileUrlResolver {

    /**
     * <p>파일 URI를 생성합니다. 파일 URI는 서버 URL을 포함하지 않습니다. 파일 URI는
     * (UUID) + "_" + (Epoch time in millis) + "." + (extension) 형식을 따릅니다.
     * <p>예를 들어 {@code path}의 값이 <code>images/example</code>이고, {@code extension}이 <code>jpg</code>라면,
     * 리턴값은 <code>images/example300ff6e8-f539-4c85-90cf-d1e740f42c36_1744634398718.jpg</code>이 될 수 있습니다.
     * <p>이 메소드로 생성된 값이 데이터베이스에 저장되어야 합니다.
     *
     * @param extension 파일 확장자. <code>.</code>을 포함하지 않습니다. ex) <code>jpg</code>, <code>png</code>,
     *                  <code>webp</code>, <code>jpeg</code>가 허용됩니다.
     * @param path 파일 경로. <code>path</code> 앞과 뒤에 <code>/</code>를 포함하지 않습니다. ex) <code>images/sample</code>,
     *             <code>images</code>는 허용됩니다. <code>/images</code>, <code>images/</code> 등은 허용되지 않습니다.
     * @return <code>path + "/" + UUID + "_" + Epoch_time_in_millis + "." + extension</code>
     * @throws IllegalArgumentException <code>path</code> 혹은 <code>extension</code>이 유효하지 않을 경우
     */
    String generateUuidFileUri(String extension, String path) throws IllegalArgumentException;

    /**
     * <p>파일을 업로드할 위치를 생성합니다. 애플리케이션 실행 환경에 따라 파일을 업로드하는 위치가 달라집니다.
     *
     * @param fileUri 파일 URI
     * @return 파일을 업로드할 위치
     * @throws NullPointerException <code>fileUri</code>가 <code>null</code>일 경우
     */
    String generateFileUploadUrl(String fileUri) throws NullPointerException;

    /**
     * 파일이 저장될(된) 위치를 반환합니다.
     *
     * @param fileClassification 파일이 이미지 파일인지, 동영상 파일인지, PDF 파일인지 등등
     */
    String getFileLocation(FileClassification fileClassification);
}
