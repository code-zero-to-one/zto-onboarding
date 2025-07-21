package com.codezerotoone.mvp.domain.member.member.exception;

import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public class MemberNotFoundException extends NoSuchElementException {
    private final Long memberId;

    public MemberNotFoundException(Long memberId) {
        this.memberId = memberId;
    }

    public MemberNotFoundException(String s, Throwable cause, Long memberId) {
        super(s, cause);
        this.memberId = memberId;
    }

    public MemberNotFoundException(Throwable cause, Long memberId) {
        super(cause);
        this.memberId = memberId;
    }

    public MemberNotFoundException(String s, Long memberId) {
        super(s);
        this.memberId = memberId;
    }
}
