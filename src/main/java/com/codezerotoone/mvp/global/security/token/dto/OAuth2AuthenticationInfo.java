package com.codezerotoone.mvp.global.security.token.dto;

import lombok.Builder;

@Builder
public record OAuth2AuthenticationInfo(
        String id
) {
}
