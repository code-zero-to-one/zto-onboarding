package com.codezerotoone.mvp.global.security.token.vendor;

/**
 * OAuth 2.0 Authorization Server 목록을 정리한 enum.
 *
 * @author PGD
 */
public enum AuthVendor {
    KAKAO,
    GOOGLE,
    NATIVE;

    public static AuthVendor valueOfIgnoreCase(String value) throws IllegalArgumentException {
        return valueOf(value.toUpperCase());
    }
}
