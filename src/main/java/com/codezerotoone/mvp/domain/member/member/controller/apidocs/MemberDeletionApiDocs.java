package com.codezerotoone.mvp.domain.member.member.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
        summary = "[미구현] 회원탈퇴",
        description = "회원탈퇴를 처리합니다. 탈퇴된 회원은 복구할 수 있습니다.",
        parameters = @Parameter(
                in = ParameterIn.PATH,
                description = "수정할 회원의 ID",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "회원 탈퇴 성공",
                        content = @Content(examples = @ExampleObject("""
                                {
                                    "statusCode": 200,
                                    "timestamp": "2025-03-30T12:12:30.013",
                                    "content": null,
                                    "message": null
                                }
                                """))
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "존재하지 않는 회원",
                        content = @Content(
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 404,
                                            "errorName": "MEMBER_NOT_FOUND",
                                            "errorCode": "MEM002",
                                            "detail": 1000,
                                            "timestamp": "2025-03-30T12:12:30.013"
                                        }
                                        """)
                        )
                )
        }
)
public @interface MemberDeletionApiDocs {
}
