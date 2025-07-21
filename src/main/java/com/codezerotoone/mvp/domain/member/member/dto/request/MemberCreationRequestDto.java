package com.codezerotoone.mvp.domain.member.member.dto.request;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.codezerotoone.mvp.global.jackson.imageextension.ImageExtensionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class MemberCreationRequestDto {

    @Schema(
            name = "loginId",
            description = "로그인 시 사용될 ID (혹은 소셜 로그인 ID). 소셜 로그인 시 생략된다.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
//    @NotEmpty(message = "\"loginId\" should not be empty")
    // TODO: 소셜 로그인 시 loginId를 비울 것인가, 아니면 어떻게 할 것인가
    private String loginId;

    @Schema(
            name = "name",
            description = "회원의 이름",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "\"name\" should not be empty. (+ not null)")
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "\"name\" should follow the pattern: ^[가-힣a-zA-Z]{2,10}$")
    private String name;

    @Schema(
            name = "imageExtension",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "이미지 확장자 - jpg, jpeg, png 등. "
    )
    @JsonDeserialize(using = ImageExtensionDeserializer.class)
    private ImageExtension imageExtension;
}
