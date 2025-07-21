package com.codezerotoone.mvp.global.api.error;

import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class CommonErrorHandlingControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        CommonErrorCode.INVALID_PARAMETER,
                        ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map((fieldError) ->
                                        Map.of(
                                                "paramName", fieldError.getField(),
                                                "validationMessage", fieldError.getDefaultMessage()
                                        ))
                                .toList()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.of(
                                CommonErrorCode.INVALID_PARAMETER,
                                Map.of("message", e.getMessage())
                        )
                );
    }
}
