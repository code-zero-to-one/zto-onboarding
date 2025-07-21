package com.codezerotoone.mvp.global.api.error.docs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 에러 코드에 대한 문서화용 DTO
 * description: 설명
 * errorResponse: 정적인 에러 응답 DTO
 *
 * @author PGD
 */
@AllArgsConstructor
@Getter
@ToString
class DocumentedErrorCodeDto {
    private String description;
    private StaticErrorCodeDto errorResponse;
}
