package com.codezerotoone.mvp.domain.member.member.controller.apidocs;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
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
        summary = "[관리자] 회원 목록 조회",
        description = "탈퇴되지 않은 회원을 페이지 단위로 조회합니다.",
        parameters = {
                @Parameter(
                        in = ParameterIn.QUERY,
                        name = "page",
                        description = "페이지 번호 (0부터 시작)",
                        schema = @Schema(type = "integer", defaultValue = "0")
                ),
                @Parameter(
                        in = ParameterIn.QUERY,
                        name = "size",
                        description = "페이지 크기",
                        schema = @Schema(type = "integer", defaultValue = "20")
                ),
                @Parameter(
                        in = ParameterIn.QUERY,
                        name = "sort",
                        description = "정렬 기준 (예: createdAt,desc)",
                        schema = @Schema(type = "string")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "회원 목록 조회 성공",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        implementation = com.codezerotoone.mvp.domain.member.member.dto.MemberListDto.class
                                )
                        )
                ),
                @ApiResponse(responseCode = "403", description = "권한이 없는 경우")
        }
)
public @interface MemberListApiDocs {}
