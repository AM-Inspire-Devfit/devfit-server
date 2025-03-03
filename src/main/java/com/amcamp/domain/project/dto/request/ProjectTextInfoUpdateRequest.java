package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectTextInfoUpdateRequest(
        @Schema(description = "프로젝트 ID", example = "1") @NotNull Long projectId,
        @Schema(description = "수정된 텍스트", example = "new project title") @NotBlank String text) {}
