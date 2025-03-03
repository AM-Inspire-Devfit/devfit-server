package com.amcamp.domain.sprint.dto.request;

import com.amcamp.domain.project.domain.ToDoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record SprintToDoUpdateRequest(
        @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "스프린트 시작 날짜", defaultValue = "2026-02-01")
                LocalDate startDt,
        @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "스프린트 마감 날짜", defaultValue = "2026-03-01")
                LocalDate dueDt,
        @Schema(description = "스프린트 진행 상태", defaultValue = "NOT_STARTED") ToDoStatus status) {}
