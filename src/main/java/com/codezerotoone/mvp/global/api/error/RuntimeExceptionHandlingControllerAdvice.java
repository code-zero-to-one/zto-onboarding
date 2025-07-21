package com.codezerotoone.mvp.global.api.error;

import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class RuntimeExceptionHandlingControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException e) {
        log.error("{}", e.getMessage(), e);
        // TODO: 500 에러가 발생할 경우, 슬랙에 알림이 가는 기능 구현
        // 코드상으로 구현해도 되고, 로그 모니터링 시스템으로 구현해도 되고
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
