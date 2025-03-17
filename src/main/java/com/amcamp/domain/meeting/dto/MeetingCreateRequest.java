package com.amcamp.domain.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MeetingCreateRequest(
        @Schema(description = "미팅 타이틀", example = "중간 점검 회의") @NotBlank String title,
        @Schema(description = "미팅 날짜, 시간", example = "회의 일시") @NotNull LocalDateTime meetingDate) {}
