package com.codezerotoone.mvp.domain.member.memberprofile.dto.request;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.domain.member.memberprofile.constant.Mbti;
import com.codezerotoone.mvp.global.jackson.imageextension.ImageExtensionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
public class MemberProfileUpdateRequestDto {

    @Schema(description = "회원 이름; \"ignore-null\"이 false인 경우, null이면 안 됨",
            pattern = "^[가-힣a-zA-Z]{2,10}$",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "\"name\" should follow the pattern: ^[가-힣a-zA-Z]{2,10}$")
    private String name;

    @Schema(description = "연락처; \"ignore-null\"이 false인 경우, null이면 안 됨",
            pattern = "^\\d{1,3}-\\d{3,4}-\\d{4}$",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\d{1,3}-\\d{3,4}-\\d{4}$", message = "\"tel\" should follow the pattern: ^\\d{1,3}-\\d{3,4}-\\d{4}$")
    private String tel;

    @Schema(description = "GitHub 링크", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String githubLink;

    @Schema(description = "블로그/SNS 등 링크", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String blogOrSnsLink;

    @Schema(description = "한 마디 소개", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String simpleIntroduction;

    @Schema(description = "MBTI", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Mbti mbti;

    @Schema(description = "관심사 리스트 - 기존 데이터는 날아가고 이걸로 대체", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> interests;

    @Schema(description = "프로필 이미지 확장자. null이 아닐 경우 프로필 이미지 업로드 URL이 반환됨. null일 경우 반환되지 않음",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonDeserialize(using = ImageExtensionDeserializer.class)
    private ImageExtension profileImageExtension;
}
