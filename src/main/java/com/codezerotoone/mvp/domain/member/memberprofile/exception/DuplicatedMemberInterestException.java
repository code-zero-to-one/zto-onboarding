package com.codezerotoone.mvp.domain.member.memberprofile.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class DuplicatedMemberInterestException extends RuntimeException {
    private final Collection<String> duplicated;
}
