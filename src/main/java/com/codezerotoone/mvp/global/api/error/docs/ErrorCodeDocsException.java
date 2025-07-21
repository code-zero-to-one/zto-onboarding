package com.codezerotoone.mvp.global.api.error.docs;

/**
 * 에러 코드 문서화 관련 공통 Exception 클래스
 */
class ErrorCodeDocsException extends RuntimeException {

    public ErrorCodeDocsException(String message) {
        super(message);
    }

    public ErrorCodeDocsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCodeDocsException(Throwable cause) {
        super(cause);
    }
}
