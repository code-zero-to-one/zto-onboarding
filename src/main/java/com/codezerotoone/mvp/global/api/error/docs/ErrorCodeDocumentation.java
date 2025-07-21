package com.codezerotoone.mvp.global.api.error.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 에러 코드 Enum Class에 문서화 이름을 정의하기 위한 커스텀 어노테이션
 * name 값으로 문서화 시 그룹 이름으로 활용 가능
 *
 * @author PGD
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorCodeDocumentation {

    String name() default "";
}
