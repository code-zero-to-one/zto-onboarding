package com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs;

import com.codezerotoone.mvp.domain.member.memberprofile.controller.schema.MemberProfileForStudySchema;
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
        summary = "[CS 스터디 신청하기 팝업] 회원 프로필 조회",
        description = "스터디 신청할 때 필수적으로 입력해야 하는 회원 정보를 가져옵니다.",
        responses = {
                @ApiResponse(
                        description = "회원 프로필 조회 성공",
                        responseCode = "200",
                        content = @Content(
                                schema = @Schema(implementation = MemberProfileForStudySchema.class),
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 200,
                                            "content": {
                                                "memberId": 2500,
                                                "selfIntroduction": "안녕하십니까<br>공부 열심히 하는 사람입니다.",
                                                "studyPlan": "여러모로 공부할 예정입니다.",
                                                "preferredStudySubjectId": "CS_DEEP",
                                                "availableStudyTimeIds": [
                                                    1, 3, 5
                                                ],
                                                "availableTechStackIds": [
                                                    5, 6, 8
                                                ],
                                                "tel": "010-1234-1234",
                                                "githubLink": {
                                                    "url": "https://github.com/rudeh1253",
                                                    "iconUrl": "https://s3.com/icons/image/github.png",
                                                    "type": "GITHUB"
                                                },
                                                "blogOrSnsLink":
                                                    "url": "https://velog.io/@rudeh1253/posts"
                                                    "iconUrl": "https://s3.com/icons/image/blog.png",
                                                    "type": "BLOG_OR_SNS"
                                                }
                                            },
                                            "message": "blogOrSnsLink"
                                        }
                                        """)
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
public @interface GettingMemberProfileForStudyApiDocs {
}
