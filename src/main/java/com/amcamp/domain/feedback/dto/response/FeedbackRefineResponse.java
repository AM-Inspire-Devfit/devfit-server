package com.amcamp.domain.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FeedbackRefineResponse(
        @Schema(description = "AI가 개선한 피드백 메시지", example = "이번 프로젝트에서 협업이 조금 더 원활했으면 좋았을 것 같습니다.")
                String refinedMessage) {}
