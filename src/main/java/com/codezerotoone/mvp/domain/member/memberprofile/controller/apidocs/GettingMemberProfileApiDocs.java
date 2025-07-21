package com.codezerotoone.mvp.domain.member.memberprofile.controller.apidocs;

import com.codezerotoone.mvp.domain.member.memberprofile.controller.schema.FullMemberProfileResponseSchema;
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
        summary = "[마이페이지] 회원 프로필 조회",
        description = "회원 프로필을 조회합니다.",
        parameters = @Parameter(
                name = "memberId",
                description = "프로필을 조회할 회원의 ID. 숫자여야 한다",
                required = true,
                in = ParameterIn.PATH
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                schema = @Schema(implementation = FullMemberProfileResponseSchema.class),
                                examples = @ExampleObject("""
                                        {
                                            "statusCode": 200,
                                            "content": {
                                                "memberId": 10000,
                                                "autoMatching": true,
                                                "studyApplied": true,
                                                "memberInfo": {
                                                    "selfIntroduction": "<p>안녕하세요, 저는 개발자를 꿈꾸고 있습니다.</p><p>잘하지는 않지만 열심히 합니다.</p>",
                                                    "studyPlan": "매일 세 시간씩 자면서 공부할 계획입니다.",.
                                                    "preferredStudySubject": {
                                                        "studySubjectId": "CS_DEEP",
                                                        "name": "CS Deep Dive"
                                                    },
                                                    "availableStudyTimes": [
                                                        {
                                                            "id": 1,
                                                            "fromTime": "09:00",
                                                            "toTIme": "12:00",
                                                            "label": "오전",
                                                            "fullLabel": "오전(09:00~12:00)"
                                                        },
                                                        {
                                                            "id": 2,
                                                            "fromTime": "18:00",
                                                            "toTIme": "21:00",
                                                            "label": "저녁",
                                                            "fullLabel": "저녁(18:00~21:00)"
                                                        },
                                                        {
                                                            "id": 6,
                                                            "fromTime": null,
                                                            "toTime": null,
                                                            "label": "시간 협의 가능",
                                                            "fullLabel": "시간 협의 가능"
                                                        }
                                                    ],
                                                    "techStacks": [
                                                        {
                                                            "techStackId": 1,
                                                            "code": "100",
                                                            "techStackName": "Apache Kafka",
                                                            "parentId": null,
                                                            "level": 1
                                                        },
                                                        {
                                                            "techStackId": 2,
                                                            "code": "101",
                                                            "techStackName": "RabbitMQ",
                                                            "parentId": null,
                                                            "level": 1
                                                        },
                                                        {
                                                            "techStackId": 3,
                                                            "code": "105",
                                                            "techStackName": "Microsoft Azure",
                                                            "parentId": null,
                                                            "level": 1
                                                        },
                                                        {
                                                            "techStackId": 4,
                                                            "code": "200",
                                                            "techStackName": "LLM",
                                                            "parentId": null,
                                                            "level": 1
                                                        }
                                                    ]
                                                },
                                                "memberProfile": {
                                                    "memberName": "장원영",
                                                    "profileImage": {
                                                        "imageId": 3000,
                                                        "resizedImages": [
                                                            {
                                                                "resizedImageId": 1000,
                                                                "resizedImageUrl": "https://s3.aws/profile-image/image1.png",
                                                                "imageSizeType": {
                                                                    "imageTypeName": "ORIGINAL",
                                                                    "width": null,
                                                                    "height": null
                                                                }
                                                            }
                                                        ]
                                                    },
                                                    "simpleIntroduction": "잘 부탁드립니다.",
                                                    "mbti": "ENTP",
                                                    "interests": [
                                                        {
                                                            "id": 1,
                                                            "name": "Self-teaching"
                                                        },
                                                        {
                                                            "id": 2,
                                                            "name": "MIT OCW"
                                                        },
                                                        {
                                                            "id": 3,
                                                            "name": "Google"
                                                        }
                                                    ],
                                                    "birthDate": "1997-09-16",
                                                    "githubLink": {
                                                        "socialMediaId": 1,
                                                        "url": "https://github.com/rudeh1253",
                                                        "iconUrl": "https://s3.com/image/github.png",
                                                        "type": "GITHUB"
                                                    },
                                                    "blogOrSnsLink": {
                                                        "socialMediaId": 2,
                                                        "url": "https://velog.io/@rudeh1253/posts",
                                                        "iconUrl": "https://s3.com/image/blog.png",
                                                        "type": "BLOG_OR_SNS"
                                                    },
                                                    "tel": "010-1234-1234"
                                                }
                                            }
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
                                            "message": "회원 정보가 존재하지 않습니다.",
                                            "detail": {
                                                "memberId": 10000
                                            }
                                        }
                                        """)
                        )
                )
        }
)
public @interface GettingMemberProfileApiDocs {
}
