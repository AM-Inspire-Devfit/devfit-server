package com.amcamp.domain.task.dto.request;

import com.amcamp.domain.project.domain.ToDoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record TaskToDoInfoUpdateRequest(
        @Schema(description = "스프린트 ID", example = "1") Long sprintId,
        @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "태스크 시작 날짜", defaultValue = "2026-02-01")
                LocalDate startDt,
        @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "태스크 마감 날짜", defaultValue = "2026-03-01")
                LocalDate dueDt,
        @Schema(description = "태스크 진행 상태", defaultValue = "COMPLETED") ToDoStatus toDoStatus) {}
