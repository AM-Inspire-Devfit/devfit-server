package com.amcamp.domain.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MeetingTitleUpdateRequest(
        @Schema(description = "수정된 미팅 타이틀", example = "중간 점검 회의") @NotBlank String title) {}
