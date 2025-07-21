package com.codezerotoone.mvp.domain.member.memberprofile.controller.errorhandler;

import com.codezerotoone.mvp.domain.member.memberprofile.controller.MemberProfileController;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.DuplicatedMemberInterestException;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.NullArgumentException;
import com.codezerotoone.mvp.domain.member.memberprofile.exception.errorcode.MemberProfileErrorCode;
import com.codezerotoone.mvp.global.api.error.CommonErrorCode;
import com.codezerotoone.mvp.global.api.format.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice(assignableTypes = MemberProfileController.class)
public class MemberProfileErrorHandlingControllerAdvice {

    @ExceptionHandler(DuplicatedMemberInterestException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMemberInterestException(DuplicatedMemberInterestException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.of(MemberProfileErrorCode.MEMBER_INTEREST_DUPLICATE,
                                Map.of("duplicatedMemberInterests", e.getDuplicated()))
                );
    }

    @ExceptionHandler(NullArgumentException.class)
    public ResponseEntity<ErrorResponse> handleNullArgumentException(NullArgumentException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.of(CommonErrorCode.INVALID_PARAMETER,
                                List.of(Map.of(
                                        "paramName", e.getParamName(),
                                        "validationMessage", e.getMessage()
                                ))
                        )
                );
    }
}
