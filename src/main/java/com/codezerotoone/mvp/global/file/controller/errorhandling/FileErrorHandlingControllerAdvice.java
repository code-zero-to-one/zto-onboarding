package com.codezerotoone.mvp.global.file.controller.errorhandling;

import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.member.exception.errorcode.MemberErrorCode;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import com.codezerotoone.mvp.global.file.controller.FileController;
import com.codezerotoone.mvp.global.file.exception.InvalidFileException;
import com.codezerotoone.mvp.global.file.exception.errorcode.FileErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = FileController.class)
@Order(0)
@Slf4j
public class FileErrorHandlingControllerAdvice {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException e) {
        log.error("Failed to save file", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(FileErrorCode.FILE_UPLOAD_FAILED));
    }

    // 이걸 Member Controller Advice에서 처리하도록 하는 게 나을까
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> memberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                ErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND, Map.of("memberId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> invalidFileException(InvalidFileException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(FileErrorCode.INVALID_FILE));
    }
}
