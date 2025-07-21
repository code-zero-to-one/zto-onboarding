package com.codezerotoone.mvp.domain.member.member.controller.errorhandler;

import com.codezerotoone.mvp.domain.member.member.controller.MemberController;
import com.codezerotoone.mvp.domain.member.member.exception.DuplicateMemberException;
import com.codezerotoone.mvp.domain.member.member.exception.MemberNotFoundException;
import com.codezerotoone.mvp.domain.member.member.exception.NotYetAppliedStudyException;
import com.codezerotoone.mvp.domain.member.member.exception.errorcode.MemberErrorCode;
import com.codezerotoone.mvp.domain.member.memberprofile.controller.MemberProfileController;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {
        MemberController.class,
        MemberProfileController.class
})
public class MemberErrorHandlingControllerAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        return new ResponseEntity<>(
                ErrorResponse.of(MemberErrorCode.MEMBER_NOT_FOUND, Map.of("memberId", e.getMemberId())),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMemberException(DuplicateMemberException e) {
        return new ResponseEntity<>(
                ErrorResponse.of(MemberErrorCode.DUPLICATE_MEMBER, Map.of("id", e.getId())),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(NotYetAppliedStudyException.class)
    public ResponseEntity<ErrorResponse> notYetAppliedStudyException(NotYetAppliedStudyException e) {
        return new ResponseEntity<>(
                ErrorResponse.of(MemberErrorCode.NOT_YET_APPLIED_STUDY),
                HttpStatus.CONFLICT
        );
    }
}
