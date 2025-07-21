package com.codezerotoone.mvp.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer Token",
        description = "Bearer Token",
        scheme = "bearer",
        paramName = HttpHeaders.AUTHORIZATION,
        bearerFormat = "Bearer ",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class SpringdocsConfig {
}
