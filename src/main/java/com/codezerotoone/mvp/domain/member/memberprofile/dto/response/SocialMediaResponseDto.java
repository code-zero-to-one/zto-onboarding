package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import lombok.Builder;

@Builder
public record SocialMediaResponseDto(
        Long socialMediaId,
        String url,
        String iconUrl,
        String type
) {
}
