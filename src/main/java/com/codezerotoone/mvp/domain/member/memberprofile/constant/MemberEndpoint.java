package com.codezerotoone.mvp.domain.member.memberprofile.constant;

public class MemberEndpoint {
    public static final String MEMBER_PROFILE_IMAGE_PATH = "members/%d/profile/image";

    public static String generateProfileImagePath(Long memberId) {
        return String.format(MEMBER_PROFILE_IMAGE_PATH, memberId);
    }
}
