package com.codezerotoone.mvp.domain.member.member.controller.apidocs;

import com.codezerotoone.mvp.domain.member.member.dto.request.MemberCreationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
        summary = "[회원가입/로그인 팝업] 회원가입",
        description = "회원가입을 진행하는 엔드포인트",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = MemberCreationRequestDto.class),
                        examples = {
                                @ExampleObject("""
                                        {
                                            "name": "이현서",
                                            "imageExtension": "jpg"
                                        }
                                        """)
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "회원가입 성공. \"content\" 필드에는 자동 생성된 회원의 ID가 나타난다.",
                        content = @Content(
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 201,
                                            "timestamp": "2025-03-30T12:12:30.013",
                                            "message": null,
                                            "content": {
                                                "generatedMemberId": 1000,
                                                "uploadUrl": "http://localhost:8080/files/images/93ur1344-12ra-5g23-5529f-9284jv13hh34_17428482311.jpg"
                                            }
                                        }
                                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "중복된 회원 등록",
                        content = @Content(
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 409,
                                            "timestamp": "2025-06-30T20:46:00.451254",
                                            "errorCode": "MEM003",
                                            "errorName": "DUPLICATE_MEMBER",
                                            "message": "이미 가입된 회원입니다."
                                            "detail": {
                                                "id": "1513348125"
                                            }
                                        }
                                        """)
                        )
                )
        }
)
public @interface SignUpApiDocs {
}
