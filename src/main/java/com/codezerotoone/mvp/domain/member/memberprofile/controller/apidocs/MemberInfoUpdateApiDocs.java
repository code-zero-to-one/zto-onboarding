package com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs;

import com.codezerotoone.mvp.domain.member.memberprofile.dto.response.MemberInfoUpdateResponseDto;
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
        summary = "[내 정보 수정 팝업] 회원 정보 수정",
        description = "회원 정보 수정",
        parameters = {
                @Parameter(
                        name = "memberId",
                        description = "수정할 회원의 ID",
                        required = true,
                        in = ParameterIn.PATH
                ),
                @Parameter(
                        name = "ignore-null",
                        description = "true일 경우 null은 무시 (null인 필드는 업데이트되지 않음)",
                        in = ParameterIn.QUERY
                )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                        schema = @Schema(implementation = MemberInfoUpdateResponseDto.class),
                        examples = @ExampleObject("""
                                {
                                    "selfIntroduction": "인사 오지게 박습니다",
                                    "studyPlan": "열심히 잘 어떻게 잘",
                                    "preferredStudySubjectId": "CS_DEEP",
                                    "availableStudyTimeIds": [
                                        1, 3, 5
                                    ],
                                    "techStackIds": [
                                        11, 12, 15
                                    ]
                                }
                                """)
                )
        ),
        responses = @ApiResponse(
                responseCode = "200",
                description = "회원 정보 업데이트 성공",
                content = @Content(
                        examples = @ExampleObject("""
                                {
                                    "statusCode": 200,
                                    "timestamp": "2025-06-13T16:08:58.094Z",
                                    "content": {
                                        "memberId": 2,
                                        "selfIntroduction": "인사 오지게 박습니다",
                                        "studyPlan": "열심히 잘 어떻게 잘",
                                        "preferredStudySubjectId": "CS_DEEP",
                                        "techStackIds": [
                                            11, 12, 15
                                        ],
                                        "availableStudyTimeIds": [
                                            11, 12, 15
                                        ]
                                    },
                                    "message": "내 정보 업데이트 성공"
                                }
                                """)
                )
        )
)
public @interface MemberInfoUpdateApiDocs {
}
