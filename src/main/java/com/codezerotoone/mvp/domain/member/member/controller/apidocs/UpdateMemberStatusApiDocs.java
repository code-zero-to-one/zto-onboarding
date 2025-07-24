package com.codezerotoone.mvp.domain.member.member.controller.apidocs;

import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
        summary = "회원 상태 변경",
        description = "관리자가 특정 회원의 상태를 ACTIVE/DISABLED/QUIT로 변경",
        parameters = {
                @Parameter(
                        name = "memberId",
                        in = ParameterIn.PATH,
                        description = "상태를 변경할 회원의 ID",
                        required = true
                ),
                @Parameter(
                        name = "status",
                        in = ParameterIn.QUERY,
                        description = "변경할 상태 (ACTIVE, DISABLED, QUIT)",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "회원 상태 변경 성공",
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
public @interface UpdateMemberStatusApiDocs {
}
