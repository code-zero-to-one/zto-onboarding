package com.codezerotoone.mvp.global.security.token.exception;

/**
 * Authorization Server에서 인가코드를 처리할 수 없을 때 발생하는 예외.
 *
 * @author PGD
 */
public class UnsupportedCodeException extends TokenException {

    public UnsupportedCodeException(String message) {
        super(message);
    }

    public UnsupportedCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedCodeException(Throwable cause) {
        super(cause);
    }
}
