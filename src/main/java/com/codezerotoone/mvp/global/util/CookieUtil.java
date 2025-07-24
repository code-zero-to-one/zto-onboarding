package com.codezerotoone.mvp.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public String createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build()
                .toString();
    }

    public String getCookieValue(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
