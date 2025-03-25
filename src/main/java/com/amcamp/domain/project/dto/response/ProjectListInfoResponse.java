package com.amcamp.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectListInfoResponse(
        @Schema(description = "사용자가 참여중인 프로젝트 목록") ProjectInfoResponse projectInfo,
        @Schema(description = "프로젝트 참여 여부", example = "false") boolean isParticipant,
        @Schema(description = "프로젝트 팀장 여부", example = "false") boolean isAdmin) {
    public static ProjectListInfoResponse from(
            ProjectInfoResponse projectInfo, boolean isParticipant, boolean isAdmin) {
        return new ProjectListInfoResponse(projectInfo, isParticipant, isAdmin);
    }
}
