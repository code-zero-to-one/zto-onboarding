package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ErrorCodeSpec을 문서화 가능한 형태로 변환해주는 유틸리티 클래스
 *
 * @author PGD
 */
class DynamicErrorResponseGenerator {

    /**
     * ErrorCodeSpec 클래스들을 받아서
     * 이름(또는 Annotation name) → DocumentedErrorCodeDto 리스트로 매핑하여 반환
     */
    public static Map<String, ? extends Collection<DocumentedErrorCodeDto>>
    convertToErrorResponses(Collection<? extends Class<? extends ErrorCodeSpec>> classes) {
        return classes.stream()
                .collect(Collectors.toMap(
                        (c) -> c.isAnnotationPresent(ErrorCodeDocumentation.class)
                                && StringUtils.hasText(c.getAnnotation(ErrorCodeDocumentation.class).name())
                                ? c.getAnnotation(ErrorCodeDocumentation.class).name()
                                : c.getName(),
                        (c) -> Arrays.stream(c.getEnumConstants())
                                .map((e) ->
                                        // DocumentedErrorCodeSpec 구현 여부에 따라 description 포함 여부 결정
                                        e instanceof DocumentedErrorCodeSpec
                                                ? new DocumentedErrorCodeDto(((DocumentedErrorCodeSpec) e).getDescription(), StaticErrorCodeDto.of(e))
                                                : new DocumentedErrorCodeDto(null, StaticErrorCodeDto.of(e)))
                                .toList()));
    }
}
