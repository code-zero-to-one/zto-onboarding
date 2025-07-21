package com.codezerotoone.mvp.global.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class BuildInfoConfig {

    // 빌드 정보가 없을 경우 대체 정보 제공
    @Bean
    public BuildProperties buildProperties() {
        Properties properties = new Properties();
        properties.setProperty("version", "0.0.1-개발버전");
        properties.setProperty("group", "com.codezerotoone");
        properties.setProperty("name", "zero-to-one");
        properties.setProperty("time", String.valueOf(System.currentTimeMillis()));
        properties.setProperty("artifact", "zero-to-one");
        return new BuildProperties(properties);
    }
} 