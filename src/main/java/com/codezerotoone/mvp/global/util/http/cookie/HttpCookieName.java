package com.codezerotoone.mvp.global.util.http.cookie;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum HttpCookieName {
    IS_SUCCESS("is_success"),
    USER_NAME("user_name"),
    ACCESS_TOKEN("access_token"),
    PROFILE_IMAGE_URL("profile_image_url"),
    REFRESH_TOKEN("refresh_token"),
    MEMBER_ID("member_id");

    private final String cookieName;
}
