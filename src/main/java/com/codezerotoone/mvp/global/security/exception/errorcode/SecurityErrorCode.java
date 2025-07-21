package com.codezerotoone.mvp.global.security.exception.errorcode;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum SecurityErrorCode implements ErrorCodeSpec {
    AUTHENTICATION_FAILED(401, "AUTH001", "Authentication Failed"),
    AUTHORIZATION_FAILED(403, "AUTH002", "Authorization Failed"),
    UNSUPPORTED_CODE(401, "AUTH003", "지원하지 않는 코드입니다."),
    INVALID_REFRESH_TOKEN(400, "AUTH004", "지원하지 않는 리프레시 토큰입니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;
}
