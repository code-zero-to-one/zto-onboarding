package com.codezerotoone.mvp.domain.member.member.exception.errorcode;

import com.codezerotoone.mvp.global.api.error.docs.DocumentedErrorCodeSpec;
import com.codezerotoone.mvp.global.api.error.docs.ErrorCodeDocumentation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@ErrorCodeDocumentation(name = "MemberErrorCode")
public enum MemberErrorCode implements DocumentedErrorCodeSpec {
    MEMBER_CREATION_INVALID_INPUT(400,
            "MEM001",
            "유효하지 않은 입력입니다.",
            "회원가입 시 파라미터가 유효하지 않을 경우"),
    MEMBER_NOT_FOUND(404,
            "MEM002",
            "회원 정보가 존재하지 않습니다.",
            "존재하지 않는 회원을 요청할 경우"),
    DUPLICATE_MEMBER(409,
            "MEM003",
            "이미 가입된 회원입니다.",
            "이미 가입된 회원임에도 중복해서 회원가입을 시도할 경우 발생하는 에러에 대한 코드. " +
                    "회원가입 요청은 \"ROLE_GUEST\"에 대해서만 접근이 가능하므로 기본적으로 발생하지 않지만, " +
                    "회원가입이 완료되기 전에 요청이 반복해서 전송될 경우 발생한다."),
    NOT_YET_APPLIED_STUDY(409,
            "MEM004",
            "아직 스터디를 신청하지 않았습니다.",
            "스터디 참여 신청을 하지 않았는데 자동 매칭 여부를 on으로 세팅하려고 시도할 경우 " +
                    "발생하는 에러에 대한 코드.")
    ;

    private final int statusCode;
    private final String errorCode;
    private final String message;
    private final String description;
}
