package com.amcamp.domain.project.dto.request;

import com.amcamp.domain.project.domain.ToDoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectTodoInfoUpdateRequest(
        @Schema(description = "프로젝트 ID", example = "1") @NotNull Long projectId,
        @Schema(description = "수정된 프로젝트 시작일자", example = "2024-01-01") @NotNull LocalDate startDt,
        @Schema(description = "수정된 프로젝트 마감일자", example = "2024-01-01") @NotNull LocalDate DueDt,
        @Schema(description = "수정된 프로젝트 진행상태", example = "2024-01-01") @NotNull
                ToDoStatus toDoStatus) {}
