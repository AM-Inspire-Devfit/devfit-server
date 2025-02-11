package com.amcamp.domain.team.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamCheckResponse(
        @Schema(description = "팀 아이디", example = "1") Long teamId,
        @Schema(description = "팀 이름", example = "Side Effect") String teamName) {}
