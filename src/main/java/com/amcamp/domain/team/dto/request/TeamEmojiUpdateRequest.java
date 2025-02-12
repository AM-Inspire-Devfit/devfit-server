package com.amcamp.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TeamEmojiUpdateRequest(
        @NotBlank(message = "팀 이모지는 필수 사항입니다.") @Schema(description = "팀 이모지", example = "🍇")
                String teamEmoji) {}
