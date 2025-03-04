package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectTodoDateInfoUpdateRequest(
        @Schema(description = "수정된 프로젝트 시작일자", example = "2026-03-01") @NotNull LocalDate startDt,
        @Schema(description = "수정된 프로젝트 마감일자", example = "2026-04-01") @NotNull LocalDate DueDt) {}
