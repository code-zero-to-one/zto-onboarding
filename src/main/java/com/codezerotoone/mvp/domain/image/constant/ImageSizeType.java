package com.codezerotoone.mvp.domain.image.constant;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 이미지 비율에 따른 타입을 정의한 enum
 *
 * @author PGD
 */
// TODO: 여기에 정의된 사이즈를 테이블에 넣어서 관리하는 게 좋을 것인가?
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ImageSizeType {
    ORIGINAL(null, null);

    private final Integer width;
    private final Integer height;

    public Integer width() {
        return this.width;
    }

    public Integer height() {
        return this.height;
    }
}
