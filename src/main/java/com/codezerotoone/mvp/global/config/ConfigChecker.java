package com.codezerotoone.mvp.global.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigChecker {

    private static final Logger logger = LoggerFactory.getLogger(ConfigChecker.class);

    @Autowired
    private Environment env;
    
    @Autowired
    private ApplicationContext context;
    
    @Value("${spring.datasource.url:정보 없음}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username:정보 없음}")
    private String dbUsername;
    
    @Value("${spring.datasource.driver-class-name:정보 없음}")
    private String dbDriver;
    
    @Value("${spring.jpa.hibernate.ddl-auto:정보 없음}")
    private String ddlAuto;
    
    @Autowired(required = false)
    private BuildProperties buildProperties;
    
    @PostConstruct
    public void checkConfig() {
        logger.info("\n\n════════════════════════════════════════════════════");
        logger.info("🚀 애플리케이션 설정 정보");
        logger.info("════════════════════════════════════════════════════");

        // 환경 정보
        String envFile = env.getProperty("spring.profiles.active");
        logger.info("📌 환경 정보");
        logger.info("  📍 실행환경(환경변수) : {}", envFile);
        logger.info("  📍 자바 버전 : {}", System.getProperty("java.version"));
        logger.info("  📍 JVM 벤더 : {}", System.getProperty("java.vendor"));
        
        // 데이터베이스 설정
        logger.info("\n📌 데이터베이스 설정");
        logger.info("  📍 DB 드라이버 : {}", dbDriver);
        logger.info("  📍 DB 연결 URL : {}", datasourceUrl);
        logger.info("  📍 DB 사용자명 : {}", maskPassword(dbUsername));
        logger.info("  📍 DB 비밀번호 : ********");
        logger.info("  📍 JPA DDL-Auto : {}", ddlAuto);
        logger.info("  📍 DB 이름 : {}", env.getProperty("DB_NAME", "정보 없음"));
        
        // 로깅 설정
        logger.info("\n📌 로깅 설정");
        logger.info("  📍 루트 로깅 레벨 : {}", env.getProperty("logging.level.root", "INFO"));
        logger.info("  📍 앱 로깅 레벨 : {}", env.getProperty("logging.level.com.codezerotoone", "INFO"));
        logger.info("  📍 SQL 로깅 레벨 : {}", env.getProperty("logging.level.org.hibernate.SQL", "INFO"));
        
        // Actuator 설정
        logger.info("\n📌 Actuator 설정");
        logger.info("  📍 노출된 엔드포인트 : {}", env.getProperty("management.endpoints.web.exposure.include", "정보 없음"));
        logger.info("  📍 헬스 상세 정보 : {}", env.getProperty("management.endpoint.health.show-details", "정보 없음"));
        
        // 애플리케이션 정보
        logger.info("\n📌 애플리케이션 정보");
        logger.info("  📍 애플리케이션 이름 : {}", env.getProperty("spring.application.name", "mvp"));
        
        // 빌드 정보 추가
        if (buildProperties != null) {
            logger.info("  📍 애플리케이션 버전 : {}", buildProperties.getVersion());
            logger.info("  📍 빌드 시간 : {}", buildProperties.getTime());
        } else {
            logger.info("  📍 빌드 정보 : 사용할 수 없음 (개발 모드)");
        }
        
        // 빈 개수 (어떤 컴포넌트가 로드되었는지 대략적으로 확인)
        logger.info("\n📌 컴포넌트 정보");
        logger.info("  📍 등록된 빈 개수 : {}", context.getBeanDefinitionCount());
        
        logger.info("════════════════════════════════════════════════════\n\n");
    }
    
    // 민감정보(예: 비밀번호)를 마스킹 처리하는 유틸리티 메서드
    private String maskPassword(String input) {
        if (input == null || input.isEmpty() || input.equals("정보 없음")) {
            return input;
        }
        // 사용자 이름은 그대로 보여주되, 너무 긴 경우 일부 마스킹 처리
        if (input.length() <= 4) {
            return input;
        } else {
            return input.substring(0, 2) + "***" + input.substring(input.length() - 2);
        }
    }
}
