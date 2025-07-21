package com.codezerotoone.mvp.global.config;

import com.codezerotoone.mvp.global.file.uploader.FileUploader;
import com.codezerotoone.mvp.global.file.uploader.LocalFileUploader;
import com.codezerotoone.mvp.global.file.url.FileUrlResolver;
import com.codezerotoone.mvp.global.file.url.LocalFileUrlResolver;
import com.codezerotoone.mvp.global.util.http.client.resttemplate.RestTemplateErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate defaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(FileUrlResolver.class)
    public FileUrlResolver localFileUrlResolver(@Value("${server.origin}") String serverOrigin) {
        // default로 등록되는 Bean
        return new LocalFileUrlResolver(serverOrigin);
    }

    @Bean
    @ConditionalOnMissingBean(FileUploader.class)
    public FileUploader localFileUploader(@Value("${resources.static-resource-path}") String uploadPath) {
        if (!"none".equals(uploadPath)) {
            return new LocalFileUploader(uploadPath);
        }
        String directoryRoot = getClass().getClassLoader().getResource("application.yml").getPath();
        directoryRoot = directoryRoot.substring(0, directoryRoot.lastIndexOf("/"));
        directoryRoot = directoryRoot.startsWith("/") ? directoryRoot.substring(1) : directoryRoot;
        directoryRoot += "/static";
        log.info("localFileUploader - directoryRoot={}", directoryRoot);
        return new LocalFileUploader(directoryRoot);
    }
}
