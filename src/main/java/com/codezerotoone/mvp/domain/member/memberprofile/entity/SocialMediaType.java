package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @see PrimarySocialMediaType
 */
@Entity
@Table(name = "social_media_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SocialMediaType {

    @Id
    @Column(name = "social_media_type_id", columnDefinition = "VARCHAR(20)")
    private String socialMediaTypeId;

    private String socialMediaName;
    private Long iconId; // TODO: Image entity 정의 후 매핑 변경

    public static SocialMediaType getReferenceOfPrimarySocialMediaType(PrimarySocialMediaType primarySocialMediaType) {
        SocialMediaType socialMediaType = new SocialMediaType();
        socialMediaType.socialMediaTypeId = primarySocialMediaType.name();
        return socialMediaType;
    }

    public static SocialMediaType getReference(String socialMediaTypeId) {
        SocialMediaType socialMediaType = new SocialMediaType();
        socialMediaType.socialMediaTypeId = socialMediaTypeId;
        return socialMediaType;
    }
}
