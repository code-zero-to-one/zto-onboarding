package com.codezerotoone.mvp.domain.member.auth.dto.response;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class LoginResult {
    private final boolean newMember;
    private final String accessToken;
    private final String refreshToken;
    private final String profileImageUrl;
    private final String userName;
    private final Long memberId;
}
