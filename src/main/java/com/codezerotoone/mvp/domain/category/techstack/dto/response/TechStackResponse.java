package com.codezerotoone.mvp.domain.category.techstack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TechStackResponse(
        @Schema(description = "기술스택 ID") Long techStackId,
        @Schema(description = "기술스택 이름") String techStackName,
        @Schema(description = "상위 기술스택 ID (최상위면 null)") Long parentId,
        @Schema(description = "계층 레벨") int level
) {}
