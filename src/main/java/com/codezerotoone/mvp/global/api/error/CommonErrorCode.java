package com.codezerotoone.mvp.global.api.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum CommonErrorCode implements ErrorCodeSpec {
    INVALID_PARAMETER(400, "CMM001", "Invalid Parameters"),
    INTERNAL_SERVER_ERROR(500, "CMM002", "A problem occurred inside of server."),
    RESOURCE_NOT_FOUND(404, "CMM003", "Specified resource is not found."),
    HTTP_METHOD_NOT_ALLOWED(405, "CMM004", "HTTP Method not allowed for the resource."),
    UNSUPPORTED_MEDIA_TYPE(415, "CMM005", "Media type is not supported.");

    private final int statusCode;
    private final String errorCode;
    private final String message;

    public static CommonErrorCode get(int statusCode) {
        return Arrays.stream(CommonErrorCode.values())
                .filter(code -> code.getStatusCode() == statusCode)
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }
}
