package com.codezerotoone.mvp.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class ImageDto {

    @Schema(description = "이미지 ID")
    private Long imageId;

    @Schema(description = "같은 이미지를 여러 사이즈로 리사이징하여 생성된 이미지 목록")
    private List<ResizedImageDto> resizedImages;
}
