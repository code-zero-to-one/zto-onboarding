package com.codezerotoone.mvp.domain.member.member.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MemberListDto(
        Long memberId,
        String memberName,
        LocalDateTime createdAt,
        String tel,
        String birthDate,
        String preferredSubject
) { }
