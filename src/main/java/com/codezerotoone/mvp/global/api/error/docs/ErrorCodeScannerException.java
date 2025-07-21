package com.codezerotoone.mvp.global.api.error.docs;

/**
 * 에러 코드 스캐닝 과정에서 발생하는 Exception 클래스
 *
 * @author PGD
 */
class ErrorCodeScannerException extends ErrorCodeDocsException {

    public ErrorCodeScannerException(String message) {
        super(message);
    }

    public ErrorCodeScannerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCodeScannerException(Throwable cause) {
        super(cause);
    }
}
