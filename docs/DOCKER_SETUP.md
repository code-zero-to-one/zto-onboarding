## 🚀 Docker를 이용한 백엔드 서버 실행 방법

프론트엔드 개발자는 별도의 Java, MariaDB 설치 없이 Docker만 설치하여 로컬 백엔드 환경을 손쉽게 구성할 수 있습니다.

Docker Compose 파일(docker-compose.yml) 안에 환경변수가 모두 정의되어 있어, 위 방식은 환경변수 설정이 필요하지 않습니다.

### 🐳 Step 1: Docker 설치

> **Windows 사용자**

Docker Desktop for Windows 설치
- [Docker Desktop 설치 링크](https://docs.docker.com/desktop/setup/install/windows-install/)

설치 중 WSL 2(Windows Subsystem for Linux)를 사용하라는 메시지가 나타나면 안내에 따라 설치합니다.

설치 후 반드시 로그아웃 또는 재부팅을 진행하세요.

> **Mac 사용자**

Docker Desktop for Mac 설치

Mac 사용자는 [Docker Desktop for Mac](https://docs.docker.com/desktop/setup/install/mac-install/)에서 별도로 설치하세요.


### 🚦 Step 2: 백엔드 서버 실행하기

터미널 또는 명령 프롬프트를 열고, 클론한 백엔드 프로젝트 루트 폴더로 이동한 후 다음 명령어를 실행합니다.

> **Docker 컨테이너 실행하는 명령어**

```
docker-compose up -d
```

> **로그를 통해 정상 작동 여부 확인하는 명령어**

```
docker-compose logs -f mvp-app
```

**서버 정상 실행 확인**

브라우저에서 다음 주소로 접속하여 확인할 수 있습니다.

👉 http://localhost:8080

📴 Docker 컨테이너 종료하기

서버를 종료하려면 다음 명령어를 실행하세요.

```
docker-compose down
```

⚠️ 중요: 터미널 창을 닫아도 Docker 컨테이너는 종료되지 않습니다. 반드시 docker-compose down을 통해 명시적으로 종료해주세요.

