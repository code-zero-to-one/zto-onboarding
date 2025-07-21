package com.codezerotoone.mvp.global.api.format;

import com.codezerotoone.mvp.global.api.error.ErrorCodeSpec;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 공통 에러 형식을 지정. 에러 응답을 반환해야 할 때, 이 객체에 에러 정보를 담아 보냄으로써, 클라이언트에서
 * 에러 상황에 따라 에러 처리를 진행할 수 있다.
 *
 * @param errorCode domain specific한 에러 코드. statusCode로는 모든 케이스의 문제를 나타낼 수 없기 때문에, 도메인 특화된 에러 코드를
 *                  정의한다.
 * @param errorName 에러의 이름. ErrorCode는 보통 enum에 정의하며, errorName은 enum.name() 메소드로 얻을 수 있다.
 * @param detail    에러에 관련된 추가적인 정보를 클라이언트에 제공하고자 할 때, detail에 추가 정보를 담아 반환할 수 있다.
 * @author PGD
 */
@Builder(access = AccessLevel.PRIVATE)
public record ErrorResponse(
        int statusCode,
        LocalDateTime timestamp,
        String errorCode,
        String errorName,
        String message,
        Object detail
) {

    public static ErrorResponse of(ErrorCodeSpec errorCode, Object detail) {
        return ErrorResponse.builder()
                .statusCode(errorCode.getStatusCode())
                .timestamp(LocalDateTime.now())
                .errorCode(errorCode.getErrorCode())
                .errorName(errorCode.name())
                .message(errorCode.getMessage())
                .detail(detail)
                .build();
    }

    public static ErrorResponse of(ErrorCodeSpec errorCode) {
        return of(errorCode, null);
    }
}
