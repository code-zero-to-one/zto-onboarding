package com.codezerotoone.mvp.global.security.token.exception;

/**
 * 토큰과 관련된 예외 클래스의 base class.
 *
 * @author PGD
 */
public abstract class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }
}
