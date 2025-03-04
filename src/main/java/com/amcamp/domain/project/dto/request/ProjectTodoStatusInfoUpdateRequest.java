package com.amcamp.domain.project.dto.request;

import com.amcamp.domain.project.domain.ToDoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectTodoStatusInfoUpdateRequest(
        @Schema(description = "수정된 프로젝트 진행상태", example = "COMPLETED") @NotNull
                ToDoStatus toDoStatus) {}
