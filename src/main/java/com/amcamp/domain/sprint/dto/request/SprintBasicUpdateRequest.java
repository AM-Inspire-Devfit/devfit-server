package com.amcamp.domain.sprint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SprintBasicUpdateRequest(
        @Schema(description = "스프린트 제목", example = "2차 스프린트") String title,
        @Schema(description = "중간 목표", example = "MVP 개발") String goal) {}
