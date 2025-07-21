package com.codezerotoone.mvp.global.api.error.docs;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ErrorCodeSpec 인터페이스를 구현한 클래스들을 지정된 패키지에서 스캔하는 유틸리티 클래스
 *
 * @author PGD
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ErrorCodeSpecScanner {

    /**
     * basePackageName 하위의 모든 ErrorCodeSpec 구현 클래스를 탐색
     */
    public static Collection<? extends Class<? extends ErrorCodeSpec>> findAllErrorCodeSpecClasses(String basePackageName)
            throws ErrorCodeScannerException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ErrorCodeSpec.class));
        Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackageName);

        Set<Class<? extends ErrorCodeSpec>> result = new HashSet<>();

        for (BeanDefinition bd : candidates) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                if (clazz.isEnum()) {
                    result.add((Class<? extends ErrorCodeSpec>) clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new ErrorCodeScannerException(e);
            }
        }
        return result;
    }
}
