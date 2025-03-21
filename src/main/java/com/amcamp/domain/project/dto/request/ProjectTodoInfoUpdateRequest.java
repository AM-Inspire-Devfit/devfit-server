package com.amcamp.domain.project.dto.request;

import com.amcamp.domain.project.domain.ToDoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ProjectTodoInfoUpdateRequest(
        @Schema(description = "수정된 프로젝트 마감일자", example = "2026-04-01") LocalDate dueDt,
        @Schema(description = "수정된 프로젝트 진행상태", example = "COMPLETED") ToDoStatus toDoStatus) {}
