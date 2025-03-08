package com.amcamp.domain.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OriginalFeedbackRequest(
        @NotBlank(message = "피드백 메시지는 비워둘 수 없습니다.")
                @Schema(description = "사용자가 입력한 원본 피드백 메시지", example = "이 프로젝트 진행 방식이 별로였습니다.")
                @Size(max = 300, message = "피드백 메시지는 최대 300자까지 입력할 수 있습니다.")
                String originalMessage) {}
