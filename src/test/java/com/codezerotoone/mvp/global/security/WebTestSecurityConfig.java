package com.codezerotoone.mvp.global.security;

import com.codezerotoone.mvp.global.config.security.ApiSecurityFilterChainConfig;
import com.codezerotoone.mvp.global.config.security.SecurityBeansConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApiSecurityFilterChainConfig.class,
        SecurityBeansConfig.class
})
public class WebTestSecurityConfig {
}
