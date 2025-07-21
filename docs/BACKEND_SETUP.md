### 1. 자바 설치

https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html

![[Pasted image 20250323190844.png]]

### 2. Maria DB 설치 및 환경변수

https://mariadb.org/download/?m=blendbyte&t=mariadb&p=mariadb&r=10.11.11&os=windows&cpu=x86_64&pkg=msi&mirror=blendbyte

![[Pasted image 20250323190643.png]]

저는 이렇게 세팅했습니다.
어차피 Local만 돌릴 것이기 때문에 원격 접속을 허용했고
UTF8을 기본적으로 사용하도록 했습니다.
그런데 나중에 script문에서 상단에 아마? 이런 식으로 어차피 설정할 수 있습니다.
```mysql
-- 1) 전역 기본값 수정
SET GLOBAL character_set_server = 'utf8mb4';
SET GLOBAL collation_server     = 'utf8mb4_general_ci';

-- 2) 내 세션 즉시 적용
SET NAMES utf8mb4;
SET SESSION collation_connection = 'utf8mb4_general_ci';
```

![[Pasted image 20250323191331.png]]

그리고 환경변수 설정을 해줍니다.
![[Pasted image 20250323191856.png]]


### 3. IDE 설치
IDE 설치는 생략할게요.

### 4. 환경변수 및 프로필 설정
설치한 다음에 환경 변수를 이렇게 설정해줍니다.
여러개 프로젝트 진행해도 환경변수 바꾸지 않도록
IDE에 종속적으로 환경 변수를 설정해주는 게 좋은 방식 같습니다.

로컬 환경에서 개발할 시에는 다음과 같이 해줍니다.


```
SPRING_PROFILES_ACTIVE:dev.local
DB_DRIVER=org.mariadb.jdbc.Driver
DB_HOST=mvp-db
DB_PORT=3306
DB_NAME=localhost
DB_USER=root
DB_PASSWORD=root
```

다음과 같이 개인 Spring 환경설정 프로필을 만듭니다.
저는 로컬 환경에서 ddl-auto로 해놓았습니다.
데이터베이스 스키마를 대신 만들어주길 일단 원해서
createDatabaseIfNotExist=true를 넣었습니다.

gitignore에 추적하지 않도록 해놓았습니다.

![[Pasted image 20250323194921.png]]

```yml
spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3306/zto_local_core?createDatabaseIfNotExist=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: root

  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MariaDBDialect

logging:
  level:
    root: INFO
    com.codezerotoone: INFO
    org.hibernate.SQL: INFO

h2:
  console:
    enabled: false

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
  endpoint:
    health:
      show-details: when_authorized
  info:
    env:
      enabled: true
    build:
      enabled: true
    git:
      enabled: true

info:
  app:
    environment: 로컬개발환경
```














# 5.🔧 백엔드 환경변수 설정 가이드

백엔드 개발을 위한 환경변수를 설정하는 방법을 안내합니다.

---

## ⚠️ 방법 1 (미권장)

만약 다른 프로젝트와 환경변수가 겹치지 않는다면, 자신의 컴퓨터에 직접 환경변수를 설정할 수 있습니다. (권장하지 않습니다.)

Windows 또는 Mac의 시스템 환경변수 설정에 직접 추가하면 됩니다.

---

## ✅ 방법 2 (권장)

편집기별로 환경변수를 개별 프로젝트에서 설정하는 방식입니다.

### 📌 Visual Studio Code에서 설정하는 방법

디버그 시 아래와 같은 환경변수를 사용하세요.

`.vscode/launch.json` 파일에 추가:

```json
"env": {
    "SPRING_PROFILES_ACTIVE": "dev.local",
    "DB_DRIVER": "org.mariadb.jdbc.Driver",
    "DB_HOST": "localhost",
    "DB_PORT": "3306",
    "DB_NAME": "zto_local_core",
    "DB_USER": "root",
    "DB_PASSWORD": "root"
}
```

---

### 📌 IntelliJ에서 설정하는 방법

IntelliJ에서 실행 또는 디버그 시, 아래와 같이 설정하세요.

- 메뉴에서 `Run` > `Edit Configurations`를 선택합니다.
- 실행할 Application을 선택한 후, `Environment variables`에 아래 값을 추가합니다.

```
SPRING_PROFILES_ACTIVE=dev.local;DB_DRIVER=org.mariadb.jdbc.Driver;DB_HOST=localhost;DB_PORT=3306;DB_NAME=zto_local_core;DB_USER=root;DB_PASSWORD=root
```

또는 아래와 같이 환경변수를 개별적으로 입력할 수 있습니다.

| Name                    | Value                    |
|-------------------------|--------------------------|
| SPRING_PROFILES_ACTIVE  | dev.local                |
| DB_DRIVER               | org.mariadb.jdbc.Driver  |
| DB_HOST                 | localhost                |
| DB_PORT                 | 3306                     |
| DB_NAME                 | zto_local_core           |
| DB_USER                 | root                     |
| DB_PASSWORD             | root                     |

---

### 📌 실행 명령어에 추가하는 방법

터미널에서 직접 환경변수를 설정한 후 프로젝트를 실행할 수도 있습니다.

**Windows (cmd)**:
```cmd
set SPRING_PROFILES_ACTIVE=dev.local
set DB_DRIVER=org.mariadb.jdbc.Driver
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=zto_local_core
set DB_USER=root
set DB_PASSWORD=root

./gradlew bootRun
```

**Mac / Linux (bash)**:
```bash
SPRING_PROFILES_ACTIVE=dev.local \
DB_DRIVER=org.mariadb.jdbc.Driver \
DB_HOST=localhost \
DB_PORT=3306 \
DB_NAME=zto_local_core \
DB_USER=root \
DB_PASSWORD=root \
./gradlew bootRun
```

---

위 방법 중 **방법 2 또는 실행 명령어에 추가하는 방법**을 권장합니다. 환경변수가 명확히 관리되고 프로젝트마다 독립적으로 동작하기 때문입니다.

