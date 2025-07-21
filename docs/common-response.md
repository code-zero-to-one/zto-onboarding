# 공통 응답 형식 사용 가이드

Description: API의 공통 응답 형식 사용 방법을 정리
Tag: API
Writer: PGD

# 버전 히스토리

| 버전 | 업데이트일시 (KST) | 설명 |
| --- | --- | --- |
| v1 | 2025-04-26 12:26 | 히스토리 기록 시작 |
| v2 | 2025-06-03 19:26 | 에러코드 명세 변경 |

# 개요

프론트엔드에서 일관성 있는 API 응답 처리를 위해 Envelope 패턴을 적용하여 공통 응답 형식을 정의하였다. 이를 통해 프론트엔드에서는 일관성 있게 응답 바디를 처리할 수 있고, 응답 바디에 메시지나 timestamp와 같은 데이터를 포함함으로써 프론트엔드 개발에 도움을 줄 수 있다.

[Envelope pattern을 사용하는 이유](https://programmer93.tistory.com/62)

## `BaseResponse`

일반적인 경우 사용하는 응답 바디 형식.

| 필드 | 설명 | 필수 여부 |
| --- | --- | --- |
| `statusCode` | 응답의 상태 코드 | 필수 |
| `timestamp` | 응답이 생성된 시점 | 필수 |
| `content` | 클라이언트의 요청 처리에 대한 응답 데이터 | 선택 |
| `message` | API 응답 메시지 | 선택 |

**응답 예시**

```json
{
    "statusCode": 1073741824,
    "timestamp": "2025-03-26T13:15:39.858Z",
    "content": {
        "memberId": 1000,
        "loginId": "example@gmail.com",
        "phone": "010-1234-1234"
    },
    "message": "string"
}

```

`statusCode`는 HTTP Status code, `timestamp`는 응답 시간, `content`는 반환 데이터, `message`는 응답 메시지이다. 백엔드 개발자는 `message`에 임의의 메시지를 담아서 API 호출자에게 해당 API 응답에 대한 설명을 첨언할 수 있다. `statusCode`는 필수이며, 그외에는 null이 삽입될 수 있다. (204 No Content의 경우도 있으니 `content`도 null일 수 있음 / `timestamp`는 자동으로 삽입됨)

**`BaseResponse` 객체 위치**`com.codezerotoone.mvp.global.api.format.BaseResponse`

### 사용법

모든 컨트롤러에서 `BaseResponse` 객체를 반환하면 된다. `BaseResponse` 객체는 `of` static 메소드로 생성한다.

**예시**

```java
/**
 * 회원 조회.
 */
@GetMapping("/{memberId}")
public ResponseEntity<BaseResponse<MemberResponse>> getMember(@PathVariable("memberId") Long memberId) {
    return BaseResponse.of(this.memberService.getMember(memberId), HttpStatus.OK, "회원 조회 성공");
}

```

## `ErrorResponse`

사용자의 입력갑싱 잘못됐거나, 코드 로직상 문제가 발생할 경우, 관련된 내용을 클라이언트가 정형화된 형식으로 응답받아야 한다. 이를 위해 공토 에러 응답 포맷을 정의함으로써 클라이언트에서 수월하게 에러를 처리할 수 있도록 한다.

```tsx
{
    statusCode: number;
    timestamp: Date;
    errorCode: string;
    errorName: string;
    message: string;
    detail: object;
}

```

| 필드 | 설명 | 필수여부 |
| --- | --- | --- |
| `statusCode` | 현재 에러 응답의 상태 코드 | 필수 |
| `timestamp` | 에러 응답이 반환되는 시점의 시간 | 필수 |
| `errorCode` | 에러의 코드. 상태 코드만으로 표현할 수 없는 서비스 고유의 에러 코드를 반환. | 필수 |
| `errorName` | 에러의 이름 | 필수 |
| `message` | 에러 메시지 | 필수 |
| `detail` | 에러 응답에 대한 추가적인 정보 | 선택 |

### 예시

회원 조회 시, 해당 회원이 존재하지 않을 경우 - 3000번 회원을 조회하려고 하는데, 해당 회원이 없을 경우, 알맞은 에러 응답을 클라이언트로 전송하며, 이때 몇 번 회원이 존재하지 않는지 정보 또한 에러 응답에 포함시켜야 한다고 하자.

```json
{
    "statusCode": 404,
    "timestamp": "2025-03-01H01:23:22.003",
    "errorCode": "MEM001",
    "errorName": "MEMBER_NOT_FOUND",
    "message": "해당 회원이 존재하지 않습니다.",
    "detail": {
        "memberId": 3000
    }
}

```

위와 같은 형식으로 클라이언트에 반환할 수 있다. `detail`은 에러 상황에 대한 추가적인 정보를 전달할 수 있으며, 추가 정보를 전달할 필요가 없을 경우 `null`이 응답될 수 있다.

## 자바 코드에서 구현

에러 응답을 구현하기 위해서는 다음 항목을 구현해야 한다.

- 공통 에러 응답 형식 클래스
- 에러 코드 enum
- enum의 getter를 정의한 공통 인터페이스
- 예외를 처리하는 `RestControllerAdvice`

### 에러 코드 enum

HTTP 표준 상태 코드만으로는 우리 서비스의 고유한 에러를 표현하기에 턱없이 부족하다. 그래서 상태 코드 외에 에러 코드를 따로 정의하고, 에러 코드를 통해 에러를 식별하여 그에 맞는 처리를 수행해야 한다. **에러 코드는 우리 도메인의 특성을 반영한다**.

에러 코드는 enum에서 관리할 수 있으며, 하나의 enum 클래스만 사용할 수 있으나, 에러 코드가 많아질수록 파일의 라인 수가 감당할 수 없을 만큼 커지거나 merge conflict가 발생해 관리가 어려워질 수 있다. 그래서 각 도메인에 맞게 따로 에러 코드 enum을 정의할 수 있다.

enum의 예시는 다음과 같다.

```java
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
enum MemberErrorCode implements ErrorCodeSpec {
    MEMBER_NOT_FOUND(404, "MEM001", "회원이 존재하지 않습니다."),
    INVALID_PASSWORD(401, "MEM002", "패스워드가 존재하지 않습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;
}

```

### enum의 getter를 정의한 공통 인터페이스

에러 응답을 보내는 곳에서마다 에러 응답 DTO를 생성하는 건 귀찮은 반복작업이다. 그래서 각 에러 코드에 따라 에러 응답을 생성하는 로직을 공통으로 처리할 수 있도록 해야 한다. 이때 다형성을 활용할 수 있다.

인터페이스를 다음과 같이 정의한다.

```java
public interface ErrorCodeSpec {

    int getStatusCode();

    String getErrorCode();

    String getMessage();

    String name();
}

```

이 인터페이스를 enum에서 구현한다.

```java
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
enum MemberErrorCode implements **ErrorCodeSpec** {
    MEMBER_NOT_FOUND(404, "MEM001", "회원이 존재하지 않습니다."),
    INVALID_PASSWORD(401, "MEM002", "패스워드가 존재하지 않습니다.");

    private final int statusCode;
    private final String errorCode;
    private final String message;
}

```

공통 에러 코드를 정의한 enum 클래스 하나만 둔다면 인터페이스를 정의할 필요는 없겠지만, 도메인 상관없이 모든 에러 코드를 하나의 파일에 몰아넣는다면 에러 코드를 관리하기 어려워질 수 있기 때문에 에러 코드를 도메인에 따라 분산시키기 위해 공통 인터페이스를 정의했다.

**에러 코드와 관련된 사항은 다음 문서에서 추가 기술한다: [에러 코드 가이드](https://www.notion.so/207fbb391d7980e28ce6e800f4e5ac2f?pvs=21)** 

### 공통 에러 응답 형식 클래스

공통 에러 응답은 다음과 같이 구현될 수 있다.

```java
@RequiredArgsConstructor
@Builder
@Getter
@ToString
public class ErrorResponse {
    private final int statusCode;
    private final String errorCode;
    private final String errorName;
    private final String message;
    private final LocalDateTime timestamp;
    private final Object detail;
}

```

`ErrorResponse`의 값은 `detail`을 제외하고 모두 에러 응답 enum에 정의된 데이터에서 가져올 수 있는 것들이다. 그런데 에러 응답 enum은 `ErrorCodeSpec`이라는 인터페이스를 구현하고 있다. 그래서 `ErrorResponse`에서 `ErrorCodeSpec`에 의존함으로써 여러 에러 코드에 대한 에러 응답 DTO를 `ErrorResponse`에서 공통으로 처리할 수 있다.

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ErrorResponse {
    private final int statusCode;
    private final String errorCode;
    private final String errorName;
    private final String message;
    private final LocalDateTime timestamp;
    private final Object detail;

    public static ErrorResponse of(ErrorSpecifiable e) {
	      return ErrorResponse.builder()
	              .statusCode(e.getStatusCode())
	              .errorCode(e.getErrorCode())
	              .errorName(e.name())
	              .message(e.getMessage())
	              .timestamp(LocalDateTime.now())
	              .build();
    }

    public static ErrorResponse of(ErrorSpecifiable e, Object detail) {
	      return ErrorResponse.builder()
	              .statusCode(e.getStatusCode())
	              .errorCode(e.getErrorCode())
	              .errorName(e.name())
	              .message(e.getMessage())
	              .timestamp(LocalDateTime.now())
	              .detail(detail)
	              .build();
    }
}

```