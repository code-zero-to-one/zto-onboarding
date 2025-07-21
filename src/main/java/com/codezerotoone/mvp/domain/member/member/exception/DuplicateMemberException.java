package com.codezerotoone.mvp.domain.member.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DuplicateMemberException extends RuntimeException {
    private final String id; // memberId가 아닌 OICD ID 혹은 loginId

    public DuplicateMemberException(String id, Exception e) {
        super(e);
        this.id = id;
    }
}
