# OAuth 2.0 가이드

Description: 백엔드 서버의 OAuth 2.0 기반 인증/인가 사용 가이드
Tag: OAuth 2.0, Spring Security
Writer: PGD

# 버전 히스토리

| 버전 | 업데이트일시 (KST) | 설명 |
| --- | --- | --- |
| v1 | 2025-04-26 00:26 | 히스토리 기록 시작 |
| v2 | 2025-04-26 00:47 | `scope`에 대한 설명 추가 |
| v3 | 2025-04-26 19:48 | 소셜 로그인 서버에서 가져오는 사용자 정보에 대한 명세 추가 |
| v4 | 2025-04-26 20:22 | 소셜 로그인 실패 케이스 추가 |
| v5 | 2025-04-26 20:49 | JSON 토큰 설명 수정 |
| v6 | 2025-05-17 15:53 | Swagger UI에서 인증 추가하기 |
| v7 | 2025-06-14 19:53 | 프론트엔드에서 state 파라미터를 통해 리다이렉션할 프론트엔드 페이지 지정 |
| v8 | 2025-06-24 20:46 | 소셜 로그인 시 프론트엔드 redirection 페이지에 auth vendor도 전달 |

# 개요

OAuth 2.0 기반 인증/인가 가이드입니다. 이외에도 JSON 기반 인증/인가 프로세스에 대해서도 기술합니다. 먼저 프론트엔드 개발자를 위한 명세를 서술한 다음, 백엔드 개발자를 위한 명세를 작성합니다. 관련 링크는 문서 맨 아래에 나타나 있습니다.

## 용어

| 용어 | 의미 |
| --- | --- |
| Authentication | 인증, 접근 자격이 있는지 검증하는 단계를 말합니다. |
| Resource Server | 사용자의 보호된 자원을 호스팅하는 서버입니다. 즉, 저희 백엔드 서버를 말합니다. |
| Resource Owner | Resource Server의 계정을 보유하고 있는 사용자입니다. |
| Client | 보호된 자원을 사용하려고 접근 요청을 하는 애플리케이션입니다. |
| Authorization Server | 인증/인가를 수행하는 서버입니다. Authorization Server로부터 Access Token, Refresh Token 등을 발급받을 수 있습니다. 구글 로그인 서버, 카카오 로그인 서버 등을 일컫습니다. |
| 인가 코드 (Authorization code) | Access Token을 발급받을 때 사용합니다. Redirect URI로 리다이렉션될 때 쿼리 파라미터에 담겨서 전달됩니다. |
| Access Token | Authorization Server에서 발행하는 토큰으로서, 보호된 Resource에 접근하기 위해 사용되는 "입장권" 같은 역할을 수행합니다. |
| Refresh Token | Access Token이 만료되면 Refresh Token을 가지고 Access Token을 재발급받을 수 있습니다. |
| OIDC (OpenID Connect) | OAuth 2.0 프레임워크 기반 인증 프로토콜로서, 기존 OAuth 2.0 프로토콜이 Resource에 대한 접근을 제어하는 인가 (Authorization)의 역할만 수행했다면, OIDC를 적용한 OAuth 2.0 프로토콜에서는 사용자의 신원을 파악하는 인증 (Authentication)의 기능을 추가로 수행합니다. |
| ID Token | OIDC 프로토콜 명세에 따라 사용자의 인증 정보를 담고 있는 토큰입니다. JWT로 암호화된 토큰이며, header와 payload는 Base64로 인코딩되어 있기 때문에 마찬가지로 Base64로 디코딩하여 payload의 정보를 추출할 수 있습니다. |

# OAuth 2.0 사용하기

## OAuth 2.0 Roles

OAuth 2.0 기반 인증 프로토콜에 참여하는 주체들은 다음과 같습니다.

- Resource Server
    - Resource를 가지고 있는 서버로서, 저희 백엔드 서버입니다.
- Authorization Server
    - 인증/인가를 대행해 주는 서버로서, 구글 로그인 서버, 카카오 로그인 서버 등을 가리킵니다.
- Client
    - 보호된 Resource에 대한 요청을 보내는 주체로서, 웹 브라우저 등을 가리킵니다. 저희 서비스가 모바일로 확장된다면, 모바일 애플리케이션이 포함될 수 있습니다.
- Resource Owner
    - 보호된 자원을 소유하고 있으며 보호된 자원에 대한 접근 권한을 가지고 있는 사용자를 말합니다.

## 흐름

저희 서비스에서 OAuth 2.0 기반 인증 흐름은 다음과 같습니다.

![435403647-e0ac2030-a3bc-48d0-bc59-7141a5d62bf5.png](435403647-e0ac2030-a3bc-48d0-bc59-7141a5d62bf5.png)

1. Resource Owner가 Client (웹 브라우저)를 동작하여 로그인 요청을 합니다.
2. Client가 Authorization Server로 소셜 로그인을 요청합니다.
3. 웹 브라우저가 소셜 로그인 페이지로 리다이렉션됩니다. (사실 Resource Owner와 Client를 한 몸이라고 봐도 되겠습니다)
4. ID, PW를 입력합니다. 구글 로그인이면 구글 아이디, 카카오 로그인이면 카카오 아이디를 입력합니다.
5. Authorization Server에서 인가 코드를 발급합니다.
6. Redirection URI로 페이지가 리다이렉션되는데, 이 Redirect URI는 백엔드의 특정 URI를 지정하면 되겠습니다.
7. 백엔드 서버에서 인가 코드를 가지고 Access Token 발급을 요청합니다.
8. Authorization Server에서 Access Token, Refresh Token을 발급해 줍니다. OpenID Connect 서비스를 신청했다면 ID Token까지 발급해 줍니다. (현재 신청된 상태라서 ID Token도 같이 발급됩니다)
9. 프론트엔드 페이지로 리다이렉션합니다. 이때 Access Token, Refresh Token도 같이 발급해 줍니다.

## 프론트엔드에서 OAuth 2.0 프로세스 시작하기

프론트엔드에서 OAuth 2.0을 사용하기 위해서는 소셜 로그인 페이지로 리다이렉션해야 하는데요, 각 소셜 로그인 서비스에 따라 리다이렉션 방법은 다음과 같습니다.

### Redirect URI

백엔드 서버에서 지정된 Redirect URI의 path 다음과 같습니다.

`/api/v1/auth/{authVendor}/redirect-uri`

이때 `authVendor`는 카카오 로그인인지, 구글 로그인인지에 따라 `kakao`, 혹은 `google`입니다.

- 카카오 로그인: `/api/v1/auth/kakao/redirect-uri`
- 구글 로그인: `/api/v1/auth/google/redirect-uri`

### 소셜 로그인 서버에서 가져올 데이터

- 회원의 이름
- 회원의 프로필 사진

이 두 가지 데이터를 가져옵니다.

### 카카오 로그인

프론트엔드에서 카카오 로그인을 활용하는 방법은 두 가지가 있습니다.

### REST API

`https://kauth.kakao.com/oauth/authorize`로 이동시키면 됩니다. 이때 쿼리 파라미터를 추가로 전달해야 합니다. 카카오 로그인 REST API 문서에 나온 바에 나타난 쿼리 파라미터 설명은 다음과 같습니다.

| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| `client_id` | `String` | 앱 REST API 키. | O |
| `redirect_uri` | `String` | 인가 코드를 전달받을 서비스 서버의 URI. | O |
| `response_type` | `String` | 고정적으로 값을 `code`로 설정하면 됩니다. | O |
| `state` | `String` | 백엔드 서버로 추가 파라미터를 보낼 수 있으며, 문서 버전 v7 현재 `state`에 담을 수 있는 파라미터는 카카오 로그인 후 리다이렉션할 프론트엔드 Origin입니다. | X |

이외에도 `prompt`, `scope` 등이 있는데, 이는 카카오 로그인 REST API 문서를 참조하시면 되겠습니다.

**예시**

```
<https://kauth.kakao.com/oauth/authorize?client_id=faewihfipafvihpszcivh3h10ref&redirect_uri=http://localhost:8080/api/v1/auth/kakao/redirect-uri&response_type=code>
```

### JavaScript SDK 활용

카카오 로그인에서 JavaScript SDK를 제공하고 있습니다. 이를 사용하기 위해서는 웹 페이지에 jQuery와 카카오 로그인 SDK를 불러와야 합니다. HTML 의 `<script>` 태그를 사용해 CDN으로 불러올 수 있습니다. 카카오 SDK를 불러오면 `Kakao` 객체를 사용할 수 있게 됩니다. (React에서는 어떻게 하는지 모르겠습니다)
코드 예시는 아래와 같습니다.

```html
<!-- OAuth 2.0 인증을 테스트하기 위한 웹 페이지 -->
<!DOCTYPE html>
<html lang="ko">
<head>
    <script src="<https://code.jquery.com/jquery-3.7.1.min.js>"
            integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <title>소셜 로그인 테스트</title>
    <script src="<https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js>"
            integrity="sha384-TiCUE00h649CAMonG018J2ujOgDKW/kVWlChEuu4jK2vxfAAD0eZxzCKakxg55G4"
            crossorigin="anonymous"></script>
    <meta charset="UTF-8">
    <script type="text/javascript">
        $(document).ready(() => {
            const kakaoJsKey = "..."; // 카카오 로그인 JavaScript Key
            Kakao.init(kakaoJsKey);
            console.log(Kakao.isInitialized());

            $("#kakaoLoginBtn").on("click", () => {
                Kakao.Auth.authorize({
                    redirectUri: "<http://localhost:8080/api/v1/auth/kakao/redirect-uri>"
                });
            });
    </script>
</head>

<body>

<button id="kakaoLoginBtn">카카오 로그인</button>

</body>
</html>

```

### 구글 로그인

구글 로그인은 그냥 구글 로그인 페이지로 이동시키면 됩니다. 아래 "Google OAuth 2.0 사용 (웹 서버)"이라 되어 있는 링크를 클릭하면 구글 OAuth 2.0 가이드 문서가 나타납니다. 이걸 참고하시면 되겠습니다.

이동시킬 페이지 URL 예시는 다음과 같습니다.

`https://accounts.google.com/o/oauth2/v2/auth?scope=openid%20profile&access_type=offline&prompt=consent&include_granted_scopes=true&response_type=code&redirect_uri=http://localhost:8080/api/v1/auth/google/redirect-uri&client_id=...`

`client_id`에 유효한 API key를 넣으시면 되겠습니다.

`scope`는 구글 서버에서 가져올 사용자의 데이터의 범위를 지정합니다. 쿼리 파라미터 내에서 `scope` 띄어쓰기로 구분됩니다. 띄어쓰기는 URL에서 인코딩되어 `%20`로 변환됩니다.

**`scope` 목록**

- `openid`: OIDC 프로토콜상 ID
- `profile`

## 토큰 발급

위에 나타난 바와 같이 소셜 로그인을 마무리지으면 토큰이 발급됩니다. 위에서 언급한 OAuth 2.0 인증/인가 흐름을 상기해 주시기 바랍니다. 저 흐름에 따르면, 6번에서 백엔드 서버로 리다이렉션되기 때문에 **클라이언트의 현재 페이지 URL은 백엔드 URL이 됩니다**. 백엔드 서버로 인가 코드를 전송하는 시점에서 이미 프론트엔드 페이지를 벗어난 상태가 됩니다. 그렇기 때문에 백엔드에서 토큰이 응답에 담겨져 전해지는 시점에 프론트엔드에서 제어할 수 있는 것은 없습니다. 그렇기 때문에 백엔드에서는 토큰 정보를 응답할 때, 리다이렉션 응답을 클라이언트로 전송해야 합니다. 이때, 리다이렉션되는 대상 URL은 프론트엔드의 특정 페이지가 됩니다.

### 리다이렉션 위치

- `/redirection`

### 토큰 정보

리다이렉션은 독립된 GET 요청을 한 번 더 보내는 행위를 수반합니다. HTTP 프로토콜의 무상태성에 의해 리다이렉션 직전에 응답 바디에 담겨서 전달된 토큰은 날아가 버립니다. 그래서 리다이렉션 요청에 토큰 데이터를 담아서 보내야 하는데, 두 가지 방법이 있습니다 (더 있을 수도 있습니다).

- 토큰을 쿼리 파라미터에 담아서 보내는 것 => 프론트에서 쿼리 파라미터에서 토큰을 추출
- 토큰을 쿠키에 담아서 보내는 것 => 이때 쿠키는 HTTP-only 쿠키가 아닌 자바스크립트에서 접근 가능한 쿠키이며, 프론트에서 토큰 추출 (그후 쿠키 삭제)

저희 시스템에서는 첫 번째 방식을 사용합니다. 소셜 로그인 시 소셜 로그인 서버에서 가져오는 데이터는 쿠키에 담아서 프론트엔드로 전달하고, 프론트엔드에서는 전달된 쿠키에서 값을 추출하여 사용하면 되겠습니다. 또한 전달되는 쿠키는 로그인한 사용자가 가입된 회원인지, 가입하지 않은 사용자인지에 따라 달라집니다.

**쿠키**

| Cookie Name | 설명 | HTTP-only 여부 |
| --- | --- | --- |
| `refresh_token` | Refresh Token | O |

**쿼리 파라미터 - 공통**

| Parameter Name | 설명 |
| --- | --- |
| `access-token` | Access Token |
| `is-success` | 소셜 로그인 성공 여부. 값은 "true" 혹은 "false" |
| `auth-vendor` | 사용자가 이용한 소셜 로그인 서비스가  어떤 것인지 (구글 로그인 - `google`, 카카오 로그인 - `kakao`) |

**쿼리 파라미터 - 가입된 회원일 경우**

| Parameter Name | 설명 |
| --- | --- |
| `member-id` | 로그인한 회원의 `memberId` |

**쿼리 파라미터 - 가입되지 않은 회원일 경우 (가입 필요)**

| Parameter Name | 설명 |
| --- | --- |
| `user-name` | 소셜 로그인 서버에서 가져온 사용자의 이름. URI 인코딩되어 전달될 것이기 때문에 자바스크립트에서 사용하려면 `decodeURI()` 함수 같은 걸 사용해 디코딩해야 합니다. |
| `profile-image-url` | 소셜 로그인 서버에서 가져온 사용자의 프로필 사진 URL |

아래는 가입하지 않은 회원일 경우입니다.

![437704017-0fdf746e-995a-4594-a579-76bc62b18e63.png](437704017-0fdf746e-995a-4594-a579-76bc62b18e63.png)

(403 Forbidden은 신경쓰지 않으셔도 됩니다)
`user_name`을 자바스크립트의 `decodeURI()`로 디코딩해 보면,

![437701730-6b2dc994-d30d-419c-bae8-108ec3b31ef2.png](437701730-6b2dc994-d30d-419c-bae8-108ec3b31ef2.png)

이렇게 잘 나옵니다.

## 소셜 로그인 이후 리다이렉션될 프론트엔드 사이트 지정하기

상기 서술된 OAuth 2.0 흐름을 보면 마지막 9번째 단계에서 프론트엔드 페이지로 리다이렉션됩니다. 리다이렉션될 프론트엔드 페이지의 경로는 `/redirection`으로 지정돼 있습니다.

어떤 사이트로 리다이렉션될지 (`http://localhost:3000`으로 리다이렉션될지, `https://test.zeroone.it.kr`로 리다이렉션될지 등)는 백엔드 서버에 기본값이 세팅돼 있습니다.

| 백엔드 서버 종류 | 백엔드 서버 도메인 | 리다이렉션될 프론트엔드 사이트 |
| --- | --- | --- |
| 로컬 서버 | `http://localhost:8080` | `http://localhost:3000` |
| QA 서버 (스테이징 서버) | `http://test.zeroone.it.kr:9090` | `https://test.zeroone.it.kr` |
| 운영 서버 (예정) | `https://api.zeroone.it.kr` (예정) | `https://www.zeroone.it.kr` (예정) |

여기에 추가적으로 프론트엔드 개발 시 리다이렉션될 프론트엔드 사이트를 지정할 수 있습니다.

`state` 파라미터를 활용해서 리다이렉션될 프론트엔드 사이트를 지정할 수 있습니다. 예를 들어 설명드리겠습니다.

QA 서버로 예를 들자면, `state` 파라미터를 세팅하지 않고 소셜 로그인을 진행한 경우, access token과 함께 리다이렉션될 페이지 경로는 `https://test.zeroone.it.kr/redirection`입니다. 그런데 개발을 위해 배포된 프론트엔드 서버가 아니라 로컬 프론트엔드 서버 (`http://localhost:3000`)으로 리다이렉션시키고 싶습니다. 그러면 다음과 같이 `state` 파라미터를 세팅해 보겠습니다.

- 카카오 로그인: `https://kauth.kakao.com/oauth/authorize?client_id=3194796599a4325c0223d154319351a4&redirect_uri=http://test.zeroone.it.kr:9090/api/v1/auth/kakao/redirect-uri&response_type=code**&state=http://localhost:3000**`
- 구글 로그인: `https://accounts.google.com/o/oauth2/v2/auth?scope=openid%20profile&access_type=offline&prompt=consent&include_granted_scopes=true&response_type=code&redirect_uri=http://test.zeroone.it.kr:9090/api/v1/auth/google/redirect-uri&client_id=616205933420-b45d510q23togkaqo069j8igmsjhp9v0.apps.googleusercontent.com**&state=http://localhost:3000**`

위에 밑줄 친 `&state=http://localhost:3000`을 추가하시면 되겠습니다. 그리고 그대로 요청을 날려 봅시다. 한 번 시도해 보세요. 될 겁니다.

## 토큰을 포함하여 요청을 보내기

Access Token을 정상적으로 발급받았다면, 이 Access Token을 가지고 보호된 자원에 접근할 수 있습니다. Access Token은 Authorization 헤더에 Bearer Token 형식으로 담아서 전송하면 됩니다.

예시)

```
Authorization: Bearer ya29.a0AZYkNZh5Q5UvfK7iYRQCqfHQpwR...

```

인증에 성공했다면 정상적으로 백엔드 비즈니스 로직에 도달하기 됩니다.

인증/인가 프로세스에 문제가 생기는 경우는 다음과 같습니다.

- 토큰이 유효하지 않음 (중간에 문자가 빠졌거나 등) => 401 Unauthorized
- 토큰이 만료됨 => 401 Unauthorized
- 유효하지 않은 Refresh Token => 401 Unauthorized
- 토큰은 유효하지만 접근이 허용된 Resource가 아님 => 403 Forbidden

에러 응답에 대한 명세는 하단에 기재합니다.

## Refresh Token을 통한 Access Token 재발급

Access Token이 만료된 경우, Refresh Token으로 Access Token을 재발급받을 수 있습니다. 프론트엔드에서는 Axios interceptor 등을 활용해 보호된 자원에 대한 접근 시 401 에러가 발생했을 때, Access Token 재발급 엔드포인트로 요청을 보내는 방식으로 구현하시면 되겠습니다.

- 엔드포인트: `/api/v1/auth/access-token/refresh`
- HTTP Method: GET
**(Refresh Token 저장 공간 협의 후 명세 마무리)**

해당 엔드포인트의 Swagger 문서를 확인해 보시면 되겠습니다.

## JSON 토큰

개발 환경에서 매번 소셜 로그인을 통해 Access Token을 발급받기란 좀 번거로울 수 있습니다. 그래서 Access Token을 직접 작성하는 방법을 마련했습니다.
QA 환경, 배포 환경 외에 로컬 개발 환경에서는 기본적으로 JSON 토큰을 사용합니다. JSON 토큰 예시는 다음과 같습니다.

`{ "id": "feqfasvefq" }`

여기서 `id`는 인가 코드의 앞 20자리를 짤라낸 값입니다. 즉, `/api/v1/auth/google/redirect-uri?code=abcdefghijklmnopqrstuvwxyz`라는 URL로 GET 요청을 보낼 경우, `id` 값은 `abcdefghijklmnopqrst`가 됩니다. JSON 토큰을 사용하고 있을 때, 소셜 로그인 프로세스를 거치게 된다면 인가 코드의 앞 20자리 값이 데이터베이스에 사용자의 OIDC ID 값으로 저장됩니다. 이후 인증 프로세스에서 데이터베이스에 저장된 사용자의 OIDC ID 값을 바탕으로 인증을 수행하기 때문에, 이 값을 JSON 토큰의 `id` 값으로 담아서 보내면 되겠습니다.
JSON 토큰은 Authorization 헤더에 담아서 보내야 하는데, 앞에 "Bearer "를 붙여도 되고 안 붙여도 됩니다. 다시 말해, 다음 두 가지 케이스가 모두 허용됩니다.

- `Authorization: Bearer { "id": "abcdefghijklmnopqrst" }`
- `Authorization: { "id": "abcdefghijklmnopqrst" }`

JSON 토큰의 `id` 필드 값으로 인가 코드의 앞 20자리를 사용한 이유는 JSON 토큰을 사용하는 환경에서도 소셜 로그인을 지원하고자 하는 의도입니다. 사실 `id` 값은 무엇이 됐든 상관 없습니다. JSON 토큰의 `id` 필드 값과 데이터베이스에 저장된 사용자의 OIDC ID 값이 일치하기만 하면 됩니다. 백엔드로 회원가입 요청을 보낼 때 OIDC 값을 임의로 지정해서 요청을 보낼 수 있습니다. 그러면 JSON 토큰을 직접 작성하기가 한결 수월해질 수 있습니다.

다만 JSON 토큰에서 소셜 로그인을 수행하려고 할 경우 소셜 로그인 서버에서 사용자 정보를 가져올 수 없습니다. 그래서 백엔드 서버에서 사용자 이름과 프로필 이미지를 임의로 보내줄 수밖에 없습니다. 그래서 그냥 사용자 이름을 안유진으로 고정해서 보내기로 했습니다.

### 예시

먼저 소셜 로그인을 시행합니다. 다음 결과가 나타납니다.

![437705094-ad72fb40-d80e-4b06-b0fa-f34acf00d1ab.png](437705094-ad72fb40-d80e-4b06-b0fa-f34acf00d1ab.png)

`access_token`이 인코딩된 상태입니다. 이를 디코딩해 보면 다음과 같은 값이 나타납니다.

```
{ "id": "4/0Ab_5qlnaR2NaQtSBR", "name": "안유진", "profileImageUrl": "<https://dimg.donga.com/wps/NEWS/IMAGE/2020/12/09/104244741.2.jpg>"}
```

여기서 `name`과 `profileImageUrl`이 포함돼 있는 걸 볼 수 있는데, `name`하고 `profileImageUrl`은 소셜 로그인 과정에서 백엔드 서버에서 임의로 넣은 값이고, Authorization 헤더에 Access Token을 넣을 때는 딱히 필요 없습니다. Access Token에는 `id` 값만 있으면 됩니다.

### 회원가입하기

회원 가입 Swagger 문서는 다음과 같습니다. (회원가입 API 수정 예정)

![435452979-1a454fc9-7e3e-4e2f-b56c-871d027d1d49.png](435452979-1a454fc9-7e3e-4e2f-b56c-871d027d1d49.png)

`name`은 필수값입니다.

Postman으로 요청을 보내 보겠습니다. `POST /api/v1/members`는 비회원도 접근 가능하지만 Authorization 헤더에 유효한 Bearer Token 값을 넣어야 합니다.

![435452711-898fdcae-14ef-4e93-bf73-44abc8478625.png](435452711-898fdcae-14ef-4e93-bf73-44abc8478625.png)

위 사진을 보시면 제대로 요청이 처리된 것을 확인할 수 있습니다. DB를 조회해 보면 제대로 회원과 회원 프로필이 저장된 것을 확인할 수 있습니다.

회원 테이블

![435452727-ec489f06-ed01-4e0d-82fe-aa607960cf9d.png](435452727-ec489f06-ed01-4e0d-82fe-aa607960cf9d.png)

회원 프로필 테이블

![435452740-191b6f6b-6085-4e14-a32e-502cf148b5ba.png](435452740-191b6f6b-6085-4e14-a32e-502cf148b5ba.png)

그리고 앞서 언급한 바와 같이 회원가입 요청 시 `oidcId`를 임의로 지정할 수 있습니다.

![435452769-10324296-4391-4410-b6fc-f5666ebf41ae.png](435452769-10324296-4391-4410-b6fc-f5666ebf41ae.png)

위에 보시면 `oidcId`를 `123`으로 지정했는데요, 이 경우 Access Token의 값은 다음과 같이 설정할 수 있습니다.
`{"id":"123"}`

### 테스트하기

이 토큰을 제대로 사용할 수 있는지 테스트해 보겠습니다. 우선 테스트용 엔드포인트를 하나 만들어 줍니다.

![435434073-e70c0bf1-c95d-45f5-819a-370a6690960b.png](435434073-e70c0bf1-c95d-45f5-819a-370a6690960b.png)

그냥 여기로 GET 요청 보내면 "Hello, World!"라는 문자열을 text/plain 타입으로 응답해 줍니다. 우선 그냥 시도해 보겠습니다.

![435433903-3cf9f245-e0ea-4f73-91b8-aaa132edc9e2.png](435433903-3cf9f245-e0ea-4f73-91b8-aaa132edc9e2.png)

허용된 엔드포인트가 아니기 때문에 401 에러 응답이 오는 것을 확인했습니다. 그러면 이제 Authorization 헤더에 토큰을 넣어서 테스트해 보겠습니다. 토큰은 `{"id":"123"}`를 넣어도 되고, `{ "id": "4/0Ab_5qlmuLtSUyrrO0"}`를 넣어도 됩니다. JSON 토큰의 `id` 값과 데이터베이스에 저장된 OIDC ID 값만 일치하면 됩니다.

![435434083-1b2aa399-8083-4ac8-b4bc-8e9ffb091051.png](435434083-1b2aa399-8083-4ac8-b4bc-8e9ffb091051.png)

Bearer Token으로 `{"id":"123"}`를 넣었더니 200 응답이 오는 것을 확인할 수 있습니다.

![435434106-6a91815d-beb1-4e6f-9b33-a83cb18db4a9.png](435434106-6a91815d-beb1-4e6f-9b33-a83cb18db4a9.png)

`{ "id": "4/0Ab_5qlmuLtSUyrrO0"}`를 넣어도 마찬가지로 성공합니다. 그런데 만일 JSON 토큰의 값을 변조한다면, 401 에러가 발생합니다.

![435434137-accf6bba-937e-4371-a30a-09e09895692b.png](435434137-accf6bba-937e-4371-a30a-09e09895692b.png)

유효한 JSON 토큰의 `id` 값 일부를 지우고 요청을 보내자 401 에러 응답이 도달했습니다.

## Swagger UI에서 인증 정보를 추가하여 API 테스트하기

Swagger UI에서 인증 정보를 추가하여 API를 테스트할 수 있습니다.

![스크린샷 2025-05-17 154959.png](%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7_2025-05-17_154959.png)

우측에 “Authorize” 버튼을 클릭합니다.

![image.png](image.png)

인풋 창에 Bearer 토큰을 삽입합니다. 그리고 “Authorize” 버튼을 누릅니다.

![image.png](image%201.png)

로그인이 된 걸 확인할 수 있습니다. 이 상태로 API 테스트 요청을 보내면 Authorization 헤더에 Bearer Token이 삽입된 채로 요청이 전송됩니다.

## no-auth Profile

Spring Profile에 `no-auth`를 활성화시켜서 백엔드 웹 애플리케이션을 실행시킬 경우, 모든 요청에 대해 인증/인가를 요구하지 않습니다. Authorization 헤더가 없더라도 인증 실패가 발생하지 않습니다. `no-auth` Profile을 켜는 법에 대해서는 나중에 로컬에서 도커를 활용해 백엔드 애플리케이션을 실행하는 문서가 만들어지면 거기서 언급하겠습니다.

## 에러 응답 명세

### 유효하지 않은 인가 코드

- 엔드포인드: `/api/v1/auth/{authVendor}/redirect-uri`
- HTTP Method: GET
- Status Code: 401 Unauthorized
- 응답:

```
{
    "statusCode": 401,
    "timestamp": "2025-03-01H01:23:22.003",
    "errorCode": "AUTH003",
    "errorName": "UNSUPPORTED_CODE",
    "message": "지원하지 않는 코드입니다."
}

```

### 유효하지 않은 Refresh Token

- 엔드포인트: `/api/v1/auth/access-token/refresh`
- HTTP Method: GET
- Status Code: 401 Unauthorized
- 응답:

```
{
    "statusCode": 401,
    "timestamp": "2025-03-01H01:23:22.003",
    "errorCode": "AUTH004",
    "errorName": "INVALID_REFRESH_TOKEN"
    "message": "지원하지 않는 리프레시 토큰입니다."
}
```

### 인증 실패

- 엔드포인트: 보호된 자원
- Status Code: 401 Unauthorized
- 응답:

```
{
    "statusCode": 401,
    "timestamp": "2025-03-01H01:23:22.003",
    "errorCode": "AUTH001",
    "errorName": "AUTHENTICATION_FAILED"
    "message": "Authentication Failed"
}
```

### 권한 없음

- 엔드포인트: 권한이 없는 자원
- Status Code: 403 Forbidden
- 응답:

```
{
    "statusCode": 403,
    "timestamp": "2025-03-01H01:23:22.003",
    "errorCode": "AUTH002",
    "errorName": "AUTHORIZATION_FAILED"
    "message": "Authorization Failed"
}

```

## 소셜 로그인 테스트를 위한 소셜 로그인 계정 등록

카카오 로그인과 구글 로그인 API는 현재 테스트 앱으로 등록된 상태입니다. 그래서 각 소셜 로그인 콘솔에서 계정을 등록하지 않으면 소셜 로그인을 테스트할 수 없습니다. 카카오 로그인 API key 같은 경우 제 개인 계정 (BE_박경도)으로 발급받았기 때문에 제가 등록해야 합니다. 그래서 카카오 로그인 테스트 시 저에게 계정을 알려주시면 등록하겠습니다.

다만 구글 로그인 API key의 경우, 백엔드 공용 구글 계정을 사용하여 발급받았습니다. 그렇기 때문에 팀내에서 공유된 백엔드 공용 구글 계정에 로그인하여 자신의 구글 계정을 등록할 수 있습니다. 구글 계정을 등록하는 방법은 다음과 같습니다.

다음 프로세스를 밟기 전에 먼저 백엔드 계정에 로그인해야 합니다. 백엔드 계정 정보는 Google Drive에서 확인할 수 있을걸요?

### 1) 먼저 Google Cloud 콘솔에 진입합니다.

[링크](https://console.cloud.google.com/)

![435434341-d7f7ff3f-dd3c-427e-bb15-f3971cecce18.png](435434341-d7f7ff3f-dd3c-427e-bb15-f3971cecce18.png)

### 2) 왼쪽 위에 짝대기 세 개 버튼을 누르고 "APIs & Services" > "OAuth consent screen"으로 들어갑니다.

![435434437-e4d94277-6547-4889-9bc2-36aaee0b5fe7.png](435434437-e4d94277-6547-4889-9bc2-36aaee0b5fe7.png)

### 3) 왼쪽 탭에서 "Audience"로 들어갑니다.

![435434462-b2e04950-8d03-49f9-96d2-990a407225d4.png](435434462-b2e04950-8d03-49f9-96d2-990a407225d4.png)

### 4) "Test users" 아래 "Add users" 버튼을 클릭하시고 오른쪽에 나타난 뷰에서 자신의 Google 계정을 추가하고 "Save"를 누르시면 끝납니다.

![435434492-542df495-fed6-4579-93a7-f23ac872d682.png](435434492-542df495-fed6-4579-93a7-f23ac872d682.png)

# 링크

[Google OAuth 2.0 사용 (웹 서버)](https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko)[카카오 로그인 REST API](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)

- Comment
    
    
    **성진 - 회원가입/로그인 FE (25.5.16)**
    
    카카오 로그인에는 `카카오 로그인` 과 `간편 로그인` 이 존재하는데 각각 REST API, Javascript SDK 를 사용합니다.
    
    사용자가 카카오 로그인 버튼을 클릭했을 때 `Kakao.Auth.authorize()`를 호출하면, JavaScript SDK가 간편로그인을 위해 카카오톡을 실행합니다. 카카오톡이 설치되지 않은 환경에서는 카카오계정으로 로그인을 시도하며, 이후 과정은 REST API 방식을 따르게 됩니다.
    
    따라서 프론트엔드에서는 기본 방식인 REST API 방식을 먼저 구현하였고 추후 모바일 환경등을 고려해 간편 로그인 방식도 추가할 예정입니다.