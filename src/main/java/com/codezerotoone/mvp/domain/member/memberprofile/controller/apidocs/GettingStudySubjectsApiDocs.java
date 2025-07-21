package com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
        summary = "[CS 스터디 신청하기 팝업] (선호하는) 스터디 주제 모두 조회",
        description = "스터디 주제를 모두 조회합니다.",
        responses = @ApiResponse(
                description = "스터디 주제 조회 성공",
                responseCode = "200",
                content = @Content(
                        examples = @ExampleObject("""
                                {
                                    "statusCode": "200",
                                    "content": [
                                        {
                                            "studySubjectId": "CS_DEEP",
                                            "studySubjectName": "CS Deep Dive"
                                        },
                                        {
                                            "studySubjectId": "BACKEND_DEEP",
                                            "studySubjectName": "Back-end Deep Dive"
                                        },
                                        {
                                            "studySubjectId": "FRONTEND_DEEP",
                                            "studySubjectName": "Front-end Deep Dive"
                                        }
                                    ],
                                    "message": "모든 스터디 주제 조회 성공"
                                }
                                """)
                )
        )
)
public @interface GettingStudySubjectsApiDocs {
}
