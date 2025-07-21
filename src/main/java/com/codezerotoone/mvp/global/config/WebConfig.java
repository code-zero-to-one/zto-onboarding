package com.codezerotoone.mvp.global.config;

import com.codezerotoone.mvp.global.web.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${resources.static-resource-path}")
    private String staticResourcePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!"none".equals(staticResourcePath)) {
            log.info("addResourceHandlers is configured");
            File file = new File(staticResourcePath, "profile-image");
            registry.addResourceHandler("/profile-image/**")
                    .addResourceLocations("file:" + file.getAbsolutePath());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/api/v1/**");
    }
}
