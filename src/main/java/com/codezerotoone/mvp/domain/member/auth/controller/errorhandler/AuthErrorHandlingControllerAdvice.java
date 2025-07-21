package com.codezerotoone.mvp.domain.member.auth.controller.errorhandler;

import com.codezerotoone.mvp.domain.member.auth.controller.AuthController;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import com.codezerotoone.mvp.global.security.exception.errorcode.SecurityErrorCode;
import com.codezerotoone.mvp.global.security.token.exception.InvalidRefreshTokenException;
import com.codezerotoone.mvp.global.security.token.exception.UnsupportedCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

@RestControllerAdvice(assignableTypes = AuthController.class)
@Slf4j
public class AuthErrorHandlingControllerAdvice {
    private final String clientDomain;
    private final String clientOrigin;

    public AuthErrorHandlingControllerAdvice(@Value("${client.domain}") String clientDomain,
                                             @Value("${client.origin}") String clientOrigin) {
        this.clientDomain = clientDomain;
        this.clientOrigin = clientOrigin;
    }

    @ExceptionHandler(UnsupportedCodeException.class)
    public ResponseEntity<ErrorResponse> unsupportedCodeException(UnsupportedCodeException ex) {
        log.info("{}", ex.getMessage());
//        return new ResponseEntity<>(ErrorResponse.of(SecurityErrorCode.UNSUPPORTED_CODE), HttpStatus.UNAUTHORIZED);

        HttpHeaders headers = new HttpHeaders();

        String redirectionTo = UriComponentsBuilder.fromUriString(this.clientOrigin)
                .path("/redirection")
                .queryParam("type", "oauth2")
                .queryParam("is-success", false)
                .build()
                .encode()
                .toUriString();

        headers.add(HttpHeaders.LOCATION, redirectionTo);
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> invalidRefreshTokenException(InvalidRefreshTokenException ex) {
        log.info("{}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.of(
                                SecurityErrorCode.INVALID_REFRESH_TOKEN,
                                ex.getMessage()
                        )
                );
    }
}
