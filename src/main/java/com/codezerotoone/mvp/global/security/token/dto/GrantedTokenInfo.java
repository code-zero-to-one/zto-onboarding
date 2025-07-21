package com.codezerotoone.mvp.global.security.token.dto;

import com.codezerotoone.mvp.global.security.token.vendor.AuthVendor;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Authorization Server으로부터 토큰을 발급받은 이후, 토큰과 사용자 정보를 담은 DTO 객체.
 *
 * @param accessToken Access token
 * @param refreshToken Refresh token
 * @param id Authorization Server에서 발급해 주는 고유한 식별자
 * @param authVendor 토큰 발행 주체. Upper snake case. e.g.) <code>KAKAO</code>, <code>GOOGLE</code>
 */
@Builder
public record GrantedTokenInfo(
        @NotNull String accessToken,
        @Nullable String refreshToken,
        @NotNull String id,
        @NotNull AuthVendor authVendor
) {
}
