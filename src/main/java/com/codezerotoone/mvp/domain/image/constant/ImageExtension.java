package com.codezerotoone.mvp.domain.image.constant;

import lombok.Getter;

@Getter
public enum ImageExtension {
    DEFAULT, // To Default Image
    JPG,
    PNG,
    GIF,
    WEBP,
    SVG,
    JPEG;

    private final String extension;

    ImageExtension() {
        this.extension = name().toLowerCase();
    }

    public boolean isDefaultImage() {
        return this == DEFAULT;
    }
}
