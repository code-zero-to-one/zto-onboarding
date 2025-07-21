package com.codezerotoone.mvp.global.file.url;

import com.codezerotoone.mvp.global.file.constant.FileClassification;
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
    public String generateUuidFileUri(String extension, String path) throws IllegalArgumentException {
        if (isInvalidPathPattern(path) || isInvalidExtensionPattern(extension)) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }

        String uuidAsString = UUID.randomUUID().toString();
        Long epochTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder(path);
        return sb.append("/")
                .append(uuidAsString)
                .append("_")
                .append(epochTime)
                .append(".")
                .append(extension)
                .toString();
    }

    private boolean isInvalidPathPattern(String path) {
        return !pathPattern.matcher(path).matches();
    }

    private boolean isInvalidExtensionPattern(String extension) {
        return !this.extensionPattern.matcher(extension).matches();
    }

    @Override
    public String generateFileUploadUrl(String fileUri) throws NullPointerException {
        return this.serverUrl + "api/v1/files/" + fileUri;
    }

    @Override
    public String getFileLocation(FileClassification fileClassification) {
        int lastSlashIndex = this.serverUrl.lastIndexOf('/');
        return this.serverUrl.substring(0, lastSlashIndex == -1 ? this.serverUrl.length() : lastSlashIndex);
    }
}
