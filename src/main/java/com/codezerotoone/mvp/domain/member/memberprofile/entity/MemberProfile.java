package com.codezerotoone.mvp.domain.member.memberprofile.entity;

import com.codezerotoone.mvp.domain.image.entity.Image;
import com.codezerotoone.mvp.domain.member.member.entity.Member;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "member_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class MemberProfile {

    private static final Logger log = LoggerFactory.getLogger(MemberProfile.class);
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private MemberProfileData memberProfileData;

    @Embedded
    private MemberInfo memberInfo;

    /**
     * 필수값이 입력된 빌더 객체 반환
     *
     * @param member     MemberProfile 엔티티의 member
     * @param memberName 회원의 이름
     * @return member와 nickname이 입력된 MemberProfileBuilder
     */
    public static MemberProfileBuilder requiredBuilder(Member member, String memberName) {
        return builder()
                .member(member)
                .memberInfo(MemberInfo.createEmpty())
                .memberProfileData(MemberProfileData.create(memberName));
    }

    public String getMemberName() {
        return this.memberProfileData.getMemberName();
    }

    public void updateProfileImage(Image image) {
        this.memberProfileData.updateProfileImage(image);
    }

    public Image getProfileImage() {
        return this.memberProfileData.getProfileImage();
    }

    public void updatePrimarySocialMediaLink(String linkUrl, PrimarySocialMediaType primarySocialMediaType) {
        // TODO: 성능상 효율적인 방식으로 변경
        Optional<SocialMedia> socialMediaOp = this.memberProfileData.getSocialMedias().stream()
                .filter((socialMedia) -> primarySocialMediaType.name().equals(socialMedia.getSocialMediaType().getSocialMediaTypeId()))
                .findAny();

        if (socialMediaOp.isEmpty()) {
            SocialMedia newSocialMedia =
                    SocialMedia.create(linkUrl,
                            SocialMediaType.getReferenceOfPrimarySocialMediaType(primarySocialMediaType),
                            this);
            this.memberProfileData.removeSocialMedia(primarySocialMediaType.name());
            this.memberProfileData.addSocialMedia(newSocialMedia);
            return;
        }

        SocialMedia socialMedia = socialMediaOp.get();
        socialMedia.updateUrl(linkUrl);
    }

    public boolean isStudyAvailable() {
        return this.memberInfo.getSelfIntroduction() != null
                && this.memberInfo.getStudyPlan() != null
                && this.memberInfo.getPreferredStudySubject() != null
                && ObjectUtils.isEmpty(this.memberInfo.getAvailableStudyTimes())
                && ObjectUtils.isEmpty(this.memberInfo.getTechStackRefs())
                && this.memberProfileData.getTel() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberProfile that)) return false;
        return Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memberId);
    }
}
