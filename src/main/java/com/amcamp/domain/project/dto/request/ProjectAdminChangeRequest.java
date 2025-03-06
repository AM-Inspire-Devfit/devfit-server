package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectAdminChangeRequest(
        @Schema(description = "새 Admin ID", example = "1") @NotNull Long newAdminId) {}
