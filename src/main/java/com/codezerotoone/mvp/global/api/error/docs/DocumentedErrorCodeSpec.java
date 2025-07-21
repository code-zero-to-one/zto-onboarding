package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;

/**
 * 문서화 가능한 에러 코드 인터페이스
 * 기존 ErrorCodeSpec에 description 필드를 추가
 *
 * @author PGD
 */
public interface DocumentedErrorCodeSpec extends ErrorCodeSpec {

    String getDescription();
}
