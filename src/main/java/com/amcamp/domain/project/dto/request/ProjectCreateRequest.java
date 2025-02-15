package com.amcamp.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ProjectCreateRequest(
        @Schema(description = "팀 ID", example = "1", required = true)
                @JsonProperty("team_id")
                @NotNull
                Long teamId,
        @Schema(description = "프로젝트 제목", example = "Devfit", required = true)
                @JsonProperty("project_title")
                @NotBlank
                String projectTitle,
        @Schema(description = "프로젝트 목표", example = "개발자 건강을 위한 협업 툴 개발", required = true)
                @JsonProperty("project_goal")
                @NotBlank
                String projectGoal,
        @Schema(description = "프로젝트 시작 날짜", example = "2024-01-01T00:00", required = true)
                @JsonProperty("start_dt")
                @JsonFormat(shape = JsonFormat.Shape.STRING)
                @NotNull
                LocalDateTime startDt,
        @Schema(description = "프로젝트 마감 날짜", example = "2025-01-01T00:00", required = true)
                @JsonProperty("due_dt")
                @JsonFormat(shape = JsonFormat.Shape.STRING)
                @NotNull
                LocalDateTime dueDt,
        @Schema(description = "프로젝트 설명", example = "LG CNS AM Inspire Camp 사이드 프로젝트")
                @JsonProperty("project_description")
                String projectDescription) {}
