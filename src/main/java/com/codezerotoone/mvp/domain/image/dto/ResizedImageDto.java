package com.codezerotoone.mvp.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ResizedImageDto {

    @Schema(description = "리사이징 이미지 ID")
    private Long resizedImageId;

    @Schema(description = "리사이징 이미지 URL")
    private String resizedImageUrl;

    @Schema(description = "이미지 사이즈 타입")
    private ImageSizeTypeDto imageSizeType;
}
