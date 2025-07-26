package com.codezerotoone.mvp.domain.member.member.exception;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.dao.DataRetrievalFailureException;

@Getter
public class MemberIdNoValueException extends DataRetrievalFailureException {
    public MemberIdNoValueException(@Nullable String message) {
        super(message);
    }
}
