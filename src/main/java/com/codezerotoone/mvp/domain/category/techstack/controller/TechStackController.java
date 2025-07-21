package com.codezerotoone.mvp.domain.category.techstack.controller;

import com.codezerotoone.mvp.domain.category.techstack.dto.response.TechStackResponse;
import com.codezerotoone.mvp.domain.category.techstack.service.TechStackService;
import com.codezerotoone.mvp.global.api.format.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tech-stacks")
@Tag(
        name = "기술스택 API",
        description = "기술스택과 관련된 API"
)
public class TechStackController {

    private final TechStackService techStackService;

    @Operation(
            summary = "기술스택 목록 조회",
            description = "선택 가능한 기술스택 목록 조회",
            responses = @ApiResponse(
                    description = "조회 성공",
                    responseCode = "200",
                    content = @Content(examples = @ExampleObject("""
                            {
                                "statusCode": 200,
                                "timestamp": "2025-03-30T12:12:30.013",
                                "content": [
                                    {
                                        "techStackId": 1,
                                        "code": "BCD",
                                        "techStackName": "Back-end",
                                        "parentId": null,
                                        "level": 1
                                    },
                                    {
                                        "techStackId": 2,
                                        "code": "KFK",
                                        "techStackName": "Apache Kafka",
                                        "parentId": 1,
                                        "level": 2
                                    },
                                    {
                                        "techStackId": 3,
                                        "code": "JV",
                                        "techStackName": "Java",
                                        "parentId": 1,
                                        "level": 2
                                    },
                                    {
                                        "techStackId": 4,
                                        "code": "SPR",
                                        "techStackName": "Spring Framework",
                                        "parentId": 1,
                                        "level": 2
                                    },
                                    {
                                        "techStackId": 5,
                                        "code": "SPJ",
                                        "techStackName": "Spring Data JPA",
                                        "parentId": 4,
                                        "level": 3
                                    }
                                ]
                            }
                            """))
            )
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<TechStackResponse>>> getTechStacks() {
        List<TechStackResponse> techStacks = techStackService.getTechStacks();
        return ResponseEntity.ok(BaseResponse.of(techStacks, HttpStatus.OK, "기술스택 목록 조회 성공"));
    }


    @Operation(
            summary = "상위 기술스택 목록 조회",
            description = "상위 기술스택 목록을 조회합니다.",
            responses = @ApiResponse(
                    description = "조회 성공",
                    responseCode = "200",
                    content = @Content(examples = @ExampleObject("""
                            {
                                "statusCode": 200,
                                "timestamp": "2025-03-30T12:12:30.013",
                                "content": [
                                    {
                                        "techStackId": 1,
                                        "code": "BCD",
                                        "techStackName": "Back-end",
                                        "parentId": null,
                                        "level": 1
                                    },
                                    {
                                        "techStackId": 6,
                                        "code": "FED",
                                        "techStackName": "Front-end",
                                        "parentId": null,
                                        "level": 1
                                    }
                                ]
                            }
                            """))
            )
    )
    @GetMapping("/parents")
    public ResponseEntity<BaseResponse<List<TechStackResponse>>> getParentTechStacks() {
        List<TechStackResponse> parentTechStacks = techStackService.getParentTechStacks();
        return ResponseEntity.ok(BaseResponse.of(parentTechStacks, HttpStatus.OK, "상위 기술스택 목록 조회 성공"));
    }

    @Operation(
            summary = "[미사용] 기술스택 검색",
            description = "키워드로 기술스택을 검색합니다. (자동완성 용도) -> 프론트에서 전체 리스트 받아서(기술스택 목록 조회) 처리하는 쪽으로 해주세요",
            responses = @ApiResponse(
                    description = "검색 성공",
                    responseCode = "200",
                    content = @Content(examples = @ExampleObject("""
                            {
                                "statusCode": 200,
                                "timestamp": "2025-03-30T12:12:30.013",
                                "content": [
                                    {
                                        "techStackId": 3,
                                        "code": "JV",
                                        "techStackName": "Java",
                                        "parentId": 1,
                                        "level": 2
                                    },
                                    {
                                        "techStackId": 7,
                                        "code": "JS",
                                        "techStackName": "Javascript",
                                        "parentId": 6,
                                        "level": 2
                                    }
                                ]
                            }
                            """))
            )
    )
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<TechStackResponse>>> searchTechStacks(@RequestParam String keyword) {
        List<TechStackResponse> results = techStackService.searchTechStacks(keyword);
        return ResponseEntity.ok(BaseResponse.of(results, HttpStatus.OK, "기술스택 검색 성공"));
    }
}
