package com.codezerotoone.mvp.global.security.token.dto;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(
        String id,
        String name,
        String profileImageUrl
) {
}
