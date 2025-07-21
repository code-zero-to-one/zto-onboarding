package com.codezerotoone.mvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 애플리케이션 시작 클래스
 * @author HJC
 * 
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MvpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvpApplication.class, args);
    }
}
