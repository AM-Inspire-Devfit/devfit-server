package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectBasicInfoUpdateRequest(
        @Schema(description = "수정된 텍스트", example = "new project title") String title,
        @Schema(description = "수정된 텍스트", example = "new project goal") String goal,
        @Schema(description = "수정된 텍스트", example = "new project description") String description) {}
