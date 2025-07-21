package com.codezerotoone.mvp.domain.member.memberprofile.entity.dto;

import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;

public record MemberProfileAtomicUpdateDto(
        String name,
        String tel,
        String simpleIntroduction,
        Mbti mbti
) {
}
