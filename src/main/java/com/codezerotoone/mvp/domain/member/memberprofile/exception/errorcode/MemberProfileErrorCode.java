package com.codezerotoone.mvp.domain.member.memberprofile.exception.errorcode;

import com.codezerotoone.mvp.global.api.error.docs.DocumentedErrorCodeSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberProfileErrorCode implements DocumentedErrorCodeSpec {
    MEMBER_INTEREST_DUPLICATE(400, "MPR001", "관심사가 중복됐습니다", "관심사가 중복될 경우");

    private final int statusCode;
    private final String errorCode;
    private final String message;
    private final String description;
}
