package com.codezerotoone.mvp.domain.image.entity.dto;

import com.codezerotoone.mvp.domain.image.constant.ImageSizeType;

public record ResizedImageInfo(String resizedImageUrl, ImageSizeType imageSizeType) {
}
