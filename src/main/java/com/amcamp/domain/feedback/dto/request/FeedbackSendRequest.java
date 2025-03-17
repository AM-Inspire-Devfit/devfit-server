package com.amcamp.domain.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackSendRequest(
        @Schema(description = "스프린트 ID", example = "1") @NotNull(message = "스프린트 ID는 필수입니다.")
                Long sprintId,
        @Schema(description = "피드백을 받을 대상의 프로젝트 참여자 ID", example = "2")
                @NotNull(message = "receiverId는 필수입니다.")
                Long receiverId,
        @Schema(description = "보낼 피드백 메시지", example = "이번 프로젝트에서 협업 방식이 좋았습니다!")
                @NotBlank(message = "피드백 메시지는 비워둘 수 없습니다.")
                @Size(max = 600, message = "피드백 메시지는 최대 600자까지 전송할 수 있습니다.")
                String message) {}
