package com.amcamp.domain.project.dto.response;

import com.amcamp.domain.project.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ProjectInfoResponse(
        @Schema(description = "프로젝트 아이디", example = "1") Long projectId,
        @Schema(description = "프로젝트 제목", example = "Devfit") String projectTitle,
        @Schema(description = "프로젝트 목표", example = "개발자 건강을 위한 협업 툴 개발") String projectGoal,
        @Schema(description = "프로젝트 시작 날짜", example = "2024-01-01T00:00") LocalDateTime startDt,
        @Schema(description = "프로젝트 마감 날짜", example = "2025-01-01T00:00") LocalDateTime dueDt) {

    public static ProjectInfoResponse from(Project project) {
        return new ProjectInfoResponse(
                project.getId(),
                project.getTitle(),
                project.getGoal(),
                project.getToDoInfo().getStartDt(),
                project.getToDoInfo().getDueDt());
    }
}
