package com.amcamp.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectParticipationInfoResponse(
        @Schema(description = "사용자가 참여중인 프로젝트 목록") ProjectInfoResponse projectInfo,
        @Schema(description = "참여 여부", example = "false") boolean isParticipate) {}
