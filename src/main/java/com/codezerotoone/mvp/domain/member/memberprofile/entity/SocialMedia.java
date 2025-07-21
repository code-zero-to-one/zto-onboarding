package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_media")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SocialMedia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialMediaId;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_media_type_id")
    private SocialMediaType socialMediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberProfile memberProfile;

    public void updateUrl(String url) {
        this.url = url;
    }

    public static SocialMedia create(String url, SocialMediaType socialMediaType, MemberProfile memberProfile) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.url = url;
        socialMedia.socialMediaType = socialMediaType;
        socialMedia.memberProfile = memberProfile;
        return socialMedia;
    }
}
