package com.codezerotoone.mvp.domain.member.memberprofile.dto.response;

import com.codezerotoone.mvp.domain.image.dto.ImageDto;
import com.codezerotoone.mvp.domain.image.dto.ImageSizeTypeDto;
import com.codezerotoone.mvp.domain.image.dto.ResizedImageDto;
import com.codezerotoone.mvp.domain.image.entity.ResizedImage;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.PrimarySocialMediaType;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.IdNameDto;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.MemberProfileData;
import com.codezerotoone.mvp.domain.member.memberprofile.entity.SocialMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MemberProfileResponseDto(

        @Schema(description = "회원 이름")
        String memberName,

        @Schema(description = "프로필 이미지 - 리사이징된 이미지를 포함하고 있음 - 지금은 ORIGINAL 하나밖에 없음")
        ImageDto profileImage,

        @Schema(description = "한마디 소개")
        String simpleIntroduction,

        @Schema(description = "MBTI")
        Mbti mbti,

        @Schema(description = "관심사")
        List<IdNameDto> interests,

        @Schema(description = "깃헙 링크")
        SocialMediaResponseDto githubLink,

        @Schema(description = "생일, yyyy-MM-dd 형식")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "블로그/SNS 링크")
        SocialMediaResponseDto blogOrSnsLink,

        @Schema(description = "연락처 - 국제번호는 포함하지 않음")
        String tel
) {

    public static MemberProfileResponseDto of(MemberProfileData memberProfileData) {
        SocialMedia github = memberProfileData.getPrimarySocialMedia(PrimarySocialMediaType.GITHUB);
        SocialMedia blogOrSns = memberProfileData.getPrimarySocialMedia(PrimarySocialMediaType.BLOG_OR_SNS);
        return MemberProfileResponseDto.builder()
                .memberName(memberProfileData.getMemberName())
                .profileImage(
                        memberProfileData.getProfileImage() != null
                                ? new ImageDto(memberProfileData.getProfileImage().getImageId(),
                                memberProfileData.getProfileImage().getResizedImages()
                                        .stream()
                                        .filter(ResizedImage::isNotDeleted)
                                        .map((ri) ->
                                                new ResizedImageDto(
                                                        ri.getResizedImageId(),
                                                        ri.getFullResizedImageUrl(),
                                                        new ImageSizeTypeDto(
                                                                ri.getImageSizeType().name(),
                                                                ri.getImageSizeType().width(),
                                                                ri.getImageSizeType().height()
                                                        )
                                                )
                                        )
                                        .toList()
                        ) : null
                )
                .simpleIntroduction(memberProfileData.getSimpleIntroduction())
                .mbti(memberProfileData.getMbti())
                .interests(memberProfileData.getMemberInterests()
                        .stream()
                        .map((e) -> new IdNameDto(e.getMemberInterestId(), e.getName()))
                        .toList())
                .githubLink(
                        github != null
                                ? SocialMediaResponseDto.builder()
                                .socialMediaId(github.getSocialMediaId())
                                .url(github.getUrl())
                                .iconUrl(null) // TODO
                                .type(github.getSocialMediaType().getSocialMediaTypeId())
                                .build()
                                : null
                )
                .blogOrSnsLink(
                        blogOrSns != null
                                ? SocialMediaResponseDto.builder()
                                .socialMediaId(blogOrSns.getSocialMediaId())
                                .url(blogOrSns.getUrl())
                                .iconUrl(null) // TODO
                                .type(blogOrSns.getSocialMediaType().getSocialMediaTypeId())
                                .build()
                                : null
                )
                .birthDate(memberProfileData.getBirthDate())
                .tel(memberProfileData.getTel())
                .build();
    }
}
