# Spring Profiles

Description: 환경에 따른 Spring 설정
Tag: Spring Profile, 환경
Writer: PGD

# 버전 히스토리

| 버전 | 업데이트일시 (KST)     | 설명    |
|----|------------------|-------|
| v1 | 2025-07-17 19:51 | 문서 작성 |

# 개요

애플리케이션 실행 환경에 따라 애플리케이션 configuration, 등록되는 Spring Bean의 종류가 다릅니다. 이 문서에서는 활성화된 Spring Profile에 따라 애플리케이션 설정이 어떻게 달라지는지에
대해 설명합니다.

### Spring Profile 종류

| Profile name | 설명                                                                                            |
|--------------|-----------------------------------------------------------------------------------------------|
| `default`    | 특별히 Spring Profile 활성화를 명시하지 않을 경우 활성화되는 Profile                                              |
| `dev.local`  | 개발자 개인 개발 환경에서 개별 세팅이 필요할 때 사용하는 Profile. `.gitignore`에 등록되어 있어 Remote Repository에 올라가지 않습니다. |
| `dev.share`  | 공용 개발 서버에서 사용되는 Profile. 현재 공용 개발 서버가 없는 만큼, 의미 없는 Profile입니다.                                |
| `qa.test`    | QA 환경 (Stage 환경)에서 사용되는 Profile.                                                              |
| `prod`       | 운영 환경에서 사용되는 Profile.                                                                         |
| `no-auth`    | 인증/인가 기능을 끌 때 사용하는 Profile.                                                                   |
| `test`       | JUnit 환경에서 적용되는 Profile                                                                       |

# Profile별 설명

## `default`

기본적으로 적용되는 Spring Profile입니다. `default` profile이 활성화되었을 때 다음과 같은 효과가 나타납니다.

- JSON Token 적용 (OAuth 2.0 가이드 문서에서 JSON Token 부분을 참고해 주세요)
- 로컬 파일 저장
- `MemberTmpController` 등록
- Swagger UI
- Maria DB 사용

## `dev.local`

개인 개발 환경에 따라 필요한 설정을 하면 되겠습니다. `application-dev.local.yml` 파일은 `.gitignore`에 등록되어 있기 때문에 Remote Repository에 올라가지 않습니다.

## `dev.share`

현재 사용하지 않습니다.

## `qa.test`

QA 서버에서 적용되는 Spring Profile입니다. 다음 효과가 나타납니다.

- OAuth 2.0 적용
- 로컬 파일 저장 (나중에 On-premise 파일 서버 혹은 AWS S3 등에 저장하는 걸로 변경 예정)
- Swagger UI
- Maria DB 사용 (Container)

## `prod`

운영 서버에서 적용되는 Spring Profile입니다. 다음 효과가 나타납니다.

- OAuth 2.0 적용
- 로컬 파일 저장 (나중에 On-premise 파일 서버 혹은 AWS S3 등에 저장하는 걸로 변경 예정)
- Swagger UI가 노출되지 않음
- Maria DB 사용 (RDS)

## `no-auth`

인증/인가 로직을 적용하지 않습니다. 모든 엔드포인트에 대해 `Authorization` 헤더가 없더라도 접근 가능해집니다.

## `test`

JUnit 테스트 코드에서 사용하는 Profile입니다.

- H2 Database (내장 데이터베이스) 사용

# Profile별 등록되는 주요 Spring Bean

※ “*” (asterisk)가 붙은 Bean Type은 저희 코드베이스에서 정의된 타입입니다.

| Bean                               | Bean Type                  | 역할                                                                               | 등록 조건                                                                             |
|------------------------------------|----------------------------|----------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| `noAuthFilterChain`                | `SecurityFilterChain`      | 인증 정보를 요구하지 않는 Security Filter Chain                                             | `@Profile("no-auth")`                                                             |
| `defaultFilterChain`               | `SecurityFilterChain`      | API에 기본적으로 적용되는 Security Filter Chain                                            | `@Profile("!no-auth")`                                                            |
| `defaultAuthenticationEntryPoint`  | `AuthenticationEntryPoint` | 기본적으로 적용되는 Authentication Error 핸들러                                              | `@ConditionalOnMissingBean(AuthenticationEntryPoint.class)`                       |
| `defaultAccessDeniedHandler`       | `AccessDeniedHandler`      | 기본적으로 적용되는 Authorization Error 핸들러                                               | `@ConditionalOnMissingBean(AccessDeniedHandler.class)`                            |
| `defaultOpaqueTokenIntrospector`   | `OpaqueTokenIntrospector`  | 기본적으로 적용되는 OAuth 2.0 Bearer Token 탐색기                                            | `@ConditionalOnMissingBean(OpaqueTokenIntrospector.class)`                        |
| `defaultBearerTokenResolver`       | `BearerTokenResolver`      | 기본적으로 적용되는 Bearer Token Resolver                                                 | `@ConditionalOnMissingBean(BearerTokenResolver.class)`                            |
| `jsonTokenSupport`                 | `TokenSupport`*            | JSON 토큰을 사용하는 Bearer 토큰 핸들러                                                      | `@ConditionalOnMissingBean(TokenSupport.class)`                                   |
| `defaultRestTemplate`              | `RestTemplate`             | 기본적으로 적용되는 `RestTemplate`                                                        | `@ConditionalOnMissingBean(RestTemplate.class)`                                   |
| `localFileUrlResolver`             | `FileUrlResolver`*         | 로컬 환경에서 파일명, 파일의 URL을 적절히 변형해 주는 객체                                              | `@ConditionalOnMissingBean(FileUrlResolver.class)`                                |
| `localFileUploader`                | `FileUploader`*            | 로컬 환경에서 파일을 업로드해 주는 객체                                                           | `@ConditionalOnMissingBean(FileUploader.class)`                                   |
| `jsonBearerTokenResolver`          | `BearerTokenResolver`      | JSON 토큰을 사용할 경우 적용되는 Bearer Token Resolver                                       | `@Profile("!qa.test & !prod")`                                                    |
| `delegatingTokenSupport`           | `TokenSupport`*            | `TokenProcessor`에게 역할을 위임하는 `TokenSupport`                                       | `@Profile("qa.test or prod")`                                                     |
| `restTemplateGoogleTokenProcessor` | `TokenProcessor`*          | `RestTemplate`을 활용해 구글 Authorization Server에서 토큰과 사용자 정보를 가져오는 `TokenProcessor`  | `@Profile("qa.test                                          or prod")`            |
| `restTemplateKakaoTokenProcessor`  | `TokenProcessor`*          | `RestTemplate`을 활용해 카카오 Authorization Server에서 토큰과 사용자 정보를 가져오는 `TokenProcessor` | `@Profile("qa.test                                                     or prod")` |