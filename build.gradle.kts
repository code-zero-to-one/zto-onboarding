// ✅ 버전 및 설정 변수 정의
val javaVersion = 21
val lombokVersion = "1.18.34"
val dotenvVersion = "3.0.0"
val mariadbVersion = "3.5.2"
val h2Version = "2.2.224"
val projectEncoding = "UTF-8"
val queryDslVersion = "5.1.0"
val mapstructVersion = "1.5.5.Final"

// ✅ Gradle 플러그인 설정
plugins {
	java // Java 프로젝트
	application // 실행 가능한 애플리케이션
	id("org.springframework.boot") version "3.4.0" // Spring Boot 플러그인
	id("io.spring.dependency-management") version "1.1.6" // 의존성 자동 관리
}

// ✅ 프로젝트 기본 정보
group = "com.codezerotoone" // 패키지 네이밍 규칙을 따르는 프로젝트 그룹 ID
version = "0.0.1-SNAPSHOT" // 프로젝트 버전

// ✅ Java 버전 설정 (JDK 21 사용)
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(javaVersion))
		vendor.set(JvmVendorSpec.AMAZON)
	}
}
application {
	mainClass.set("com.codezerotoone.mvp.MvpApplication") // 올바른 메인 클래스 경로로 수정
}
// ✅ 의존성 확장 설정
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get()) // Lombok 같은 애너테이션 프로세서 사용 가능하도록 설정
	}
}

// ✅ 프로젝트에서 사용할 라이브러리 다운로드를 위한 저장소 설정
repositories {
	mavenCentral() // 라이브러리를 다운로드할 공식 저장소 (Maven Central Repository)
}

// ✅ 프로젝트에 필요한 의존성(라이브러리) 추가
dependencies {
	// 🔹 Spring Boot 관련 의존성
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA (데이터베이스 ORM)
	implementation("org.springframework.boot:spring-boot-starter-web") // Spring MVC (REST API 개발)
	implementation("org.springframework.boot:spring-boot-starter-validation") // Spring Validation
	implementation("org.springframework.boot:spring-boot-starter-security") // Spring Security
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server") // OAuth 2.0 Resource server
	// TODO: Spring Security OAuth2 Resource Server 사용에 대해 검토해야 함
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // OAuth2 starter

	// Spring Data Redis 추가
	// implementation("org.springframework.boot:spring-boot-starter-data-redis")

	//querydsl 설정
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.1.0")

	implementation("com.querydsl:querydsl-jpa:$queryDslVersion:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:$queryDslVersion:jakarta")

	// 🔹 Lombok 설정 (코드 자동 생성 도구)
	compileOnly("org.projectlombok:lombok:$lombokVersion") // 빌드 타임에만 필요한 라이브러리
	annotationProcessor("org.projectlombok:lombok:$lombokVersion") // 애너테이션 프로세서 활성화
	testCompileOnly("org.projectlombok:lombok:$lombokVersion") // 빌드 타임에만 필요한 라이브러리
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion") // 애너테이션 프로세서 활성화

	// JSON parser
	implementation("org.json:json:20240303")

    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Lombok과 MapStruct 통합 (Lombok이 먼저 처리되도록)
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// 🔹 개발 중에만 사용할 도구 (배포 시 포함되지 않음)
	developmentOnly("org.springframework.boot:spring-boot-devtools") // 개발 편의 기능 제공

	// 🔹 테스트 관련 의존성
	testImplementation("org.springframework.boot:spring-boot-starter-test") // 테스트를 위한 기본 라이브러리
	testImplementation("org.springframework.security:spring-security-test") // Spring Security 테스트 지원
	testRuntimeOnly("org.junit.platform:junit-platform-launcher") // JUnit 테스트 런처
	testImplementation("org.awaitility:awaitility:4.2.0") // 비동기,스케줄링 테스트 지원
	// 🔹 추가 라이브러리
	// dotenv 추가
	implementation("io.github.cdimascio:dotenv-java:$dotenvVersion")

	// MariaDB 드라이버 추가
	implementation("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")

	// H2 데이터베이스 (테스트용)
	runtimeOnly("com.h2database:h2:$h2Version")

	// 빌드 정보를 위한 스프링 부트 액추에이터
	implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 스웨거 API 문서 생성
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Spring Batch 추가
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // Spring Batch 테스트 의존성 추가
    testImplementation("org.springframework.batch:spring-batch-test")

    // HNSW 라이브러리 추가
    implementation("com.github.jelmerk:hnswlib-core:1.2.1")

	// MIME 타입 체크를 위한 Apache Tika
	implementation("org.apache.tika:tika-core:3.1.0")
}

// ✅ 테스트 실행 시 JUnit 5 플랫폼 사용 설정
tasks.withType<Test>().configureEach {
	useJUnitPlatform()
	systemProperty("spring.profiles.active", "test") // 모든 테스트 JVM에 test 프로필 주입
}

// ✅ UTF-8 인코딩 설정 (한글 깨짐 방지)
tasks.withType<JavaCompile>().configureEach {
	options.encoding = projectEncoding
}

// ✅ 소스 및 리소스 디렉토리 설정 (필요한 경우만 설정)
sourceSets {
	main {
		java.setSrcDirs(listOf("src/main/java"))
		resources.setSrcDirs(listOf("src/main/resources"))
	}
	test {
		java.setSrcDirs(listOf("src/test/java"))
		resources.setSrcDirs(listOf("src/test/resources"))
	}
}

// ✅ 빌드 정보 생성 설정
springBoot {
	buildInfo()
}
