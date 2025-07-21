package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.dto.MemberProfileAtomicUpdateDto;
import com.querydsl.core.annotations.QueryEmbeddable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@QueryEmbeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberProfileData {

    /**
     * 이름
     */
    private String memberName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    /**
     * 연락처
     */
    private String tel;

    /**
     * 한 마디 소개
     */
    private String simpleIntroduction;

    /**
     * MBTI
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mbti", columnDefinition = "CHAR(4)")
    private Mbti mbti;

    @OneToMany(mappedBy = "memberProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 1000)
    private List<MemberInterest> memberInterests = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 1000)
    private List<SocialMedia> socialMedias = new ArrayList<>();

    private LocalDate birthDate;

    public static MemberProfileData create(String memberName) {
        MemberProfileData memberProfileData = new MemberProfileData();
        memberProfileData.memberName = memberName;
        return memberProfileData;
    }

    public void updateProfileImage(Image image) {
        this.profileImage = image;
    }

    public void updateAtomicValues(MemberProfileAtomicUpdateDto dto, boolean ignoreNull) {
        if (!ignoreNull || dto.name() != null) {
            this.memberName = dto.name();
        }

        if (!ignoreNull || dto.tel() != null) {
            this.tel = dto.tel();
        }

        if (!ignoreNull || dto.simpleIntroduction() != null) {
            this.simpleIntroduction = dto.simpleIntroduction();
        }

        if (!ignoreNull || dto.mbti() != null) {
            this.mbti = dto.mbti();
        }
    }

    public SocialMedia getPrimarySocialMedia(PrimarySocialMediaType primarySocialMediaType) {
        return this.socialMedias.stream()
                .filter((media) -> media.getSocialMediaType().getSocialMediaTypeId().equals(primarySocialMediaType.name()))
                .findAny()
                .orElse(null);
    }

    public void removeSocialMedia(String socialMediaTypeId) {
        this.socialMedias.removeIf((socialMedia) ->
                socialMedia.getSocialMediaType().getSocialMediaTypeId().equals(socialMediaTypeId));
    }

    public void addSocialMedia(SocialMedia socialMedia) {
        this.socialMedias.add(socialMedia);
    }

    @Deprecated
    public void replaceInterests(List<MemberInterest> newInterests) {
        for (MemberInterest memberInterest : this.memberInterests) {
            memberInterest.detachMemberProfile();
            this.memberInterests.remove(memberInterest);
        }
        this.memberInterests.addAll(newInterests);
    }
}
