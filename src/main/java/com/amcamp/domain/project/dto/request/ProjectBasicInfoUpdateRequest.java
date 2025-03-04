package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

public record ProjectBasicInfoUpdateRequest(
        @Schema(description = "수정된 텍스트", example = "new project title") @Nullable String title,
        @Schema(description = "수정된 텍스트", example = "new project goal") @Nullable String goal,
        @Schema(description = "수정된 텍스트", example = "new project description") @Nullable
                String description) {}
