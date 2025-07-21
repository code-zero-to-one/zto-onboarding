package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;

/**
 * ErrorCodeSpec의 정적 응답 DTO
 * 주로 statusCode, errorCode, errorName, message를 포함
 *
 * @author PGD
 */
record StaticErrorCodeDto(
        int statusCode,
        String errorCode,
        String errorName,
        String message
) {

    public static StaticErrorCodeDto of(ErrorCodeSpec errorCodeSpec) {
        return new StaticErrorCodeDto(
                errorCodeSpec.getStatusCode(),
                errorCodeSpec.getErrorCode(),
                errorCodeSpec.name(),
                errorCodeSpec.getMessage()
        );
    }
}
