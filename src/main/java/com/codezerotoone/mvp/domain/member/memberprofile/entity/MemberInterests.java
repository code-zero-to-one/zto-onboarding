package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import com.codezerotoone.mvp.global.util.NullSafetyUtils;

import java.util.List;

// 추가 사유: MemberInterest의 무결성과 상태를 관리하기 위해 일급 컬렉션을 추가하였습니다.
public class MemberInterests {
    private List<MemberInterest> memberInterests;

    public static List<MemberInterest> from(MemberProfileUpdateRequestDto dto, MemberProfile memberProfile) {
        List<String> interests = NullSafetyUtils.replaceEmptyIfNull(dto.getInterests());

        return new MemberInterests().memberInterests = interests.stream()
                .map((interest) -> MemberInterest.of(memberProfile, interest))
                .toList();
    }

    // 외부에서의 습관적인 참조를 최소화하기 위해 default 제한자로 생성하였습니다.
    MemberInterest get(int index) {
        return memberInterests.get(index);
    }

    int size() {
        return memberInterests.size();
    }
}
