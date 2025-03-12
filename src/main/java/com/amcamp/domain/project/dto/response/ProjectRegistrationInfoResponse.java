package com.amcamp.domain.project.dto.response;

import com.amcamp.domain.project.domain.ProjectRegistration;
import com.amcamp.domain.project.domain.ProjectRegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ProjectRegistrationInfoResponse(
        @Schema(description = "프로젝트 아이디", example = "1") Long projectId,
        @Schema(description = "프로젝트 가입 요청 아이디", example = "1") Long registrationId,
        @Schema(description = "프로젝트 가입 요청자 아이디", example = "1") Long requesterId,
        @Schema(description = "프로젝트 가입 요청 진행 상태", example = "REQUEST_PENDING")
                ProjectRegistrationStatus projectRegistrationStatus,
        @Schema(description = "최종 수정 날짜", example = "2024-01-01") LocalDate lastModifiedAt) {

    public static ProjectRegistrationInfoResponse from(ProjectRegistration projectRegistration) {
        return new ProjectRegistrationInfoResponse(
                projectRegistration.getProject().getId(),
                projectRegistration.getId(),
                projectRegistration.getRequester().getId(),
                projectRegistration.getRequestStatus(),
                projectRegistration.getUpdatedDt().toLocalDate());
    }
}
