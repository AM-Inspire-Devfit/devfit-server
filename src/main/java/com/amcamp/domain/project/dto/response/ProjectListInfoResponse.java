package com.amcamp.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ProjectListInfoResponse(
        @Schema(description = "사용자가 참여중인 프로젝트 목록") List<ProjectInfoResponse> participatingProjects,
        @Schema(description = "사용자가 참여하지 않은 프로젝트 목록")
                List<ProjectInfoResponse> nonParticipatingProjects) {}
