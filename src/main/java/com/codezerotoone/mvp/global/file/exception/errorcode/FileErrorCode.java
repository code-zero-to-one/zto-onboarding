package com.codezerotoone.mvp.global.file.exception.errorcode;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum FileErrorCode implements ErrorCodeSpec {
    FILE_UPLOAD_FAILED(500, "FILE001", "파일 업로드에 실패했습니다."),
    INVALID_FILE(400, "FILE002", "파일 타입이 적절하지 않습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;
}
