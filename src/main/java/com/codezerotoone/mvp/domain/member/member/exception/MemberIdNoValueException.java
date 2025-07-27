package com.codezerotoone.mvp.domain.member.member.exception;

import lombok.Getter;
import org.springframework.dao.DataRetrievalFailureException;

@Getter
public class MemberIdNoValueException extends DataRetrievalFailureException {
    private static final String MEMBER_ID_NO_VALUE_EXCEPTION_MESSAGE = "생성된 회원 ID를 받아 오지 못했습니다. memberId: %s";

    public MemberIdNoValueException(Long memberId) {
        super(String.format(MEMBER_ID_NO_VALUE_EXCEPTION_MESSAGE, memberId));
    }
}
