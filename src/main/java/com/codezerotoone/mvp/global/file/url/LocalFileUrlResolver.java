package com.codezerotoone.mvp.global.file.url;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.global.file.constant.FileClassification;
import com.codezerotoone.mvp.global.util.FormatValidator;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LocalFileUrlResolver implements FileUrlResolver {
    private final String serverUrl; // ex) http://localhost:8080/
    private final Pattern pathPattern;
    private final Pattern extensionPattern;

    public LocalFileUrlResolver(@Value("${server.origin}") String serverOrigin) {
        log.info("server.origin: {}", serverOrigin);
        this.serverUrl = serverOrigin + "/";
        this.pathPattern = Pattern.compile("^[^/](\\w+/)*\\w+$");
        this.extensionPattern = Pattern.compile("^\\w+$");
    }

    @Override
    public String generateUuidFileUri(String path, ImageExtension extension) throws IllegalArgumentException {
        // 변경 사유: generateUuidFileUri 메서드에서 본업 외에 알아야 할 조건 정보가 너무 많아 가독성이 낮아지므로, 구체적인 유효성 검사는 타 메서드에 맡기는 게 낫다고 생각됩니다.
        if (!isValid(path, extension)) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }

        // UUID와 밀리 초 값을 기반으로 생성된 이미지 파일명은 이미 매우 충분히 유니크하다고 생각됩니다.
        // 이미지 파일이 UUID의 중복을 염려할 정도로 중요도가 높은 데이터도 아니라고 봅니다.
        String uuidAsString = UUID.randomUUID().toString();
        Long epochTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder(path);
        return sb.append("/")
                .append(uuidAsString)
                .append("_")
                .append(epochTime)
                .append(".")
                .append(extension.getExtension())
                .toString();
    }

    // !가 아닌 부정하는 단어(invalid 등)는 가독성이 떨어지고, 구체적인 검사에서도 전부 거짓 조건(!pattern.matcher)을 기반으로 boolean 값을 반환해 기존 로직은 읽기에 복잡하다고 생각됩니다.
    private boolean isValid(String path, ImageExtension extension) {
        return isValid(path) && isValid(extension);
    }

    // isValid 메서드는 모두 파라미터에 대한 유효성 검사 결과를 기대하는 공용 메서드들이므로, 검사 대상을 메서드명에 중복해서 덧붙이는 것보다는 오버로딩하는 편이 낫다고 생각됩니다.
    private boolean isValid(String path) {
        // null 체크 및 pathPattern.matcher(path).matches() 로직은 모든 도메인에서 반복되는 유효성 검사이므로, 필요할 때마다 계속 추가하는 것보다 공통 유틸 클래스로 분리하는 게 낫다고 생각됩니다.
        return FormatValidator.isValid(path, pathPattern);
    }

    private boolean isValid(ImageExtension extension) {
        return FormatValidator.isValid(extension.getExtension(), extensionPattern);
    }

    @Override
    public String generateFileUploadUrl(String fileUri) throws NullPointerException {
        return this.serverUrl + "api/v1/files/" + fileUri;
    }

    @Override
    public String generateFileUploadUrl(String path, @Nullable ImageExtension extension) throws NullPointerException {
        return generateFileUploadUrl(generateUuidFileUri(path, extension));
    }

    @Override
    public String getFileLocation(FileClassification fileClassification) {
        int lastSlashIndex = this.serverUrl.lastIndexOf('/');
        return this.serverUrl.substring(0, lastSlashIndex == -1 ? this.serverUrl.length() : lastSlashIndex);
    }
}
