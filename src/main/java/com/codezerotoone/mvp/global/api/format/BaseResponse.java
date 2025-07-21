package com.codezerotoone.mvp.global.api.format;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * <p>공통 응답 형식.
 * <p>예)</p>
 * <code>
 *     {
 *         "statusCode": 200,
 *         "timestamp": "2025-03-09H23:00:00.222",
 *         "content": {
 *             "memberId": 200,
 *             "name": "GDP"
 *         },
 *         "message": "Hello, World!"
 *     }
 * </code>
 *
 * @param <T> 응답 처리 결과의 타입.
 * @author PGD
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class BaseResponse<T> {
    /**
     * 응답의 상태 코드.
     */
    private int statusCode;

    /**
     * 응답이 전송되는 시간을 나타내는 타임스탬프.
     */
    private LocalDateTime timestamp;

    /**
     * 응답 처리 결과, 실제 데이터를 나타내는 필드.
     */
    private T content;

    /**
     * 클라이언트로 전달되는 메시지.
     */
    private String message;

    public static <T> BaseResponse<T> of(T content, HttpStatus status, String message) {
        return of(content, status.value(), message);
    }

    public static <T> BaseResponse<T> of(T content, int statusCode, String message) {
        return new BaseResponse<>(statusCode, LocalDateTime.now(), content, message);
    }

    public static <T> BaseResponse<T> of(T content, int statusCode) {
        return of(content, statusCode, null);
    }

    public static <T> BaseResponse<T> of(T content, HttpStatus status) {
        return of(content, status, null);
    }

    public static <T> BaseResponse<T> of(int statusCode) {
        return of(null, statusCode);
    }

    public static <T> BaseResponse<T> of(HttpStatus status) {
        return of(null, status);
    }

    public static <T> BaseResponse<T> of(int statusCode, String message) {
        return of(null, statusCode, message);
    }

    public static <T> BaseResponse<T> of(HttpStatus status, String message) {
        return of(null, status, message);
    }
}
