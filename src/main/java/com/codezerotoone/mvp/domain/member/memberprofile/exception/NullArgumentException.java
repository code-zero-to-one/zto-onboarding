package com.codezerotoone.mvp.domain.member.memberprofile.exception;

import lombok.Getter;

@Getter
public class NullArgumentException extends IllegalArgumentException {
    private final String paramName;

    public NullArgumentException(String paramName, String message) {
        super(message);
        this.paramName = paramName;
    }
}
