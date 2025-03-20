package com.amcamp.domain.project.dto.response;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectParticipantInfoResponse(
        @Schema(description = "프로젝트 참여자 아이디", example = "1") Long projectParticipantId,
        @Schema(description = "프로젝트 참여자 이름", example = "정선우") String projectNickname,
        @Schema(description = "프로젝트 참여자 이미지 url", example = "PreSigned URL") String profileImageUrl,
        @Schema(description = "프로젝트 참여자 권한", example = "PROJECT_ADMIN")
                ProjectParticipantRole role) {
    public static ProjectParticipantInfoResponse from(ProjectParticipant participant) {
        return new ProjectParticipantInfoResponse(
                participant.getId(),
                participant.getProjectNickname(),
                participant.getProjectProfile(),
                participant.getProjectRole());
    }
}
