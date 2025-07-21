package com.codezerotoone.mvp.global.api.springdocs.operationservice;

import com.codezerotoone.mvp.global.api.springdocs.annotation.ConditionalHiding;
import org.springdoc.core.service.GenericParameterService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.service.RequestBodyService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p><code>ConditionalHiding</code> 어노테이션에 따라 엔드포인트를 가릴지 말지 결정하기 위해 Springdocs의
 * <code>OperationService를 확장한 클래스</code>
 * <p>Spring Profile 중에 <code>hideapi</code>가 활성화되어 있을 경우, 엔드포인트를 숨긴다.
 *
 * @author PGD
 * @see ConditionalHiding
 * @see OperationService
 */
@Component
@Primary
public class RedefinedOperationService extends OperationService {
    private static final String PROFILE_NAME = "hideapi";

    private final boolean hideApi;

    public RedefinedOperationService(GenericParameterService parameterBuilder,
                                     RequestBodyService requestBodyService,
                                     SecurityService securityParser,
                                     PropertyResolverUtils propertyResolverUtils,
                                     Environment environment) {
        super(parameterBuilder, requestBodyService, securityParser, propertyResolverUtils);
        this.hideApi = Arrays.asList(environment.getActiveProfiles()).contains(PROFILE_NAME);
    }

    @Override
    public boolean isHidden(Method method) {
        if (method.getAnnotation(ConditionalHiding.class) != null) {
            return this.hideApi;
        }
        return super.isHidden(method);
    }
}
