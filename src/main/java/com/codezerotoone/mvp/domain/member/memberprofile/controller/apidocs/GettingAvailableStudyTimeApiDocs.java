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
        summary = "[내 정보 수정 팝업] 가능 시간대 전체 목록 조회",
        description = "가능 시간대 전체 목록을 조회합니다. 가능 시간대 목록 개수는 고정돼 있습니다. 시간순으로 정렬됩니다.",
        responses = @ApiResponse(
                description = "조회 성공",
                responseCode = "200",
                content = @Content(
                        examples = @ExampleObject("""
                                {
                                    "statusCode": 200,
                                    "content": [
                                        {
                                            "availableTimeId": 1,
                                            "display": "오전(09:00~12:00)"
                                        },
                                        {
                                            "availableTimeId": 2,
                                            "display": "점심(12:00~13:00)"
                                        },
                                        {
                                            "availableTimeId": 3,
                                            "display": "오후(13:00~18:00)"
                                        },
                                        {
                                            "availableTimeId": 4,
                                            "display": "저녁(18:00~21:00)"
                                        },
                                        {
                                            "availableTimeId": 5,
                                            "display": "심야(21:00~23:00)"
                                        },
                                        {
                                            "availableTimeId": 6,
                                            "display": "시간 협의 가능"
                                        }
                                    ]
                                }
                                """)
                )
        )
)
public @interface GettingAvailableStudyTimeApiDocs {
}
