package com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs;

import com.codezerotoone.mvp.domain.member.memberprofile.controller.schema.MemberProfileUpdateResponseSchema;
import com.codezerotoone.mvp.domain.member.memberprofile.dto.request.MemberProfileUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
        summary = "[내 프로필 수정 팝업] 회원 프로필 업데이트",
        description = "회원 프로필을 업데이트합니다.",
        parameters = {
                @Parameter(
                        name = "memberId",
                        in = ParameterIn.PATH,
                        description = "회원의 ID. 숫자가 와야 함"
                ),
                @Parameter(
                        name = "ignore-null",
                        in = ParameterIn.QUERY,
                        description = "true일 경우 null인 필드 반영, false일 경우 null인 필드 무시. Default=false"
                )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                        schema = @Schema(implementation = MemberProfileUpdateRequestDto.class),
                        examples = @ExampleObject("""
                                {
                                    "name": "조진세",
                                    "tel": "010-1234-1234",
                                    "githubLink": "https://github.com/rudeh1253",
                                    "blogOrSnsLink": "https://velog.io/@rudeh1253/posts",
                                    "simpleIntroduction": "백엔드 개발자입니다.",
                                    "mbti": "ENTP",
                                    "interests": [
                                        "Spring Cloud",
                                        "Spring Batch",
                                        "Apache Kafka",
                                        "Computer Science"
                                    ],
                                    "profileImageExtension": "jpg"
                                }
                                """)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                schema = @Schema(implementation = MemberProfileUpdateResponseSchema.class),
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 200,
                                            "content": {
                                                "memberId": 10000,
                                                "name": "조진세",
                                                "profileImageUploadUrl": "http://localhost:8080/profile-image/451524fwe-415942-rf913.png",
                                                "tel": "010-1234-1234",
                                                "githubLink": "https://github.com/rudeh1253",
                                                "blogOrSnsLink": "https://velog.io/@rudeh1253/posts",
                                                "simpleIntroduction": "백엔드 개발자입니다.",
                                                "mbti": "ENTP",
                                                "interests": [
                                                    {
                                                        "id": 1,
                                                        "name": "Java 24"
                                                    },
                                                    {
                                                        "id": 2,
                                                        "name": "AWS Certification"
                                                    },
                                                    {
                                                        "id": 7,
                                                        "name": "Spring Cloud"
                                                    },
                                                    {
                                                        "id": 8,
                                                        "name": "Spring Batch"
                                                    },
                                                    {
                                                        "id": 9,
                                                        "name": "Apache Kafka"
                                                    },
                                                    {
                                                        "id": 10,
                                                        "name": "Computer Science"
                                                    }
                                                ]
                                            }
                                        }
                                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        content = @Content(
                                examples = {
                                        @ExampleObject(
                                                description = "회원 관심사 중복 시",
                                                value = """
                                                        {
                                                            "statusCode": 400,
                                                            "errorCode": "MPR001",
                                                            "message": "관심사가 중복됐습니다",
                                                            "errorName": "MEMBER_INTEREST_DUPLICATE",
                                                            "detail": {
                                                                "duplicatedMemberInterests": [
                                                                    "축구", "발야구"
                                                                ]
                                                            }
                                                        }
                                                        """
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "memberId에 해당하는 회원이 없을 경우",
                        content = @Content(
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 404,
                                            "timestamp": "2025-06-30T20:46:00.451254",
                                            "errorCode": "MEM001",
                                            "errorName": "MEMBER_NOT_FOUND",
                                            "message": "회원 정보가 존재하지 않습니다."
                                            "detail": {
                                                "memberId": 10000
                                            }
                                        }
                                        """)
                        )
                )
        }
)
public @interface MemberProfileUpdateApiDocs {
}
