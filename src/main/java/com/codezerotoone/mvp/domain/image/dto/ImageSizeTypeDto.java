package com.codezerotoone.mvp.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ImageSizeTypeDto {

    @Schema(description = "리사이징 이미지 타입명")
    private String imageTypeName;

    @Schema(description = "너비 - ORIGINAL일 경우 null")
    private Integer width;

    @Schema(description = "높이 - ORIGINAL일 경우 null")
    private Integer height;
}
