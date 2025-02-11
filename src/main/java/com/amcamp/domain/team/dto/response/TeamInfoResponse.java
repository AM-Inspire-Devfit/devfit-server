package com.amcamp.domain.team.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInfoResponse(
        @Schema(description = "팀 아이디", example = "1") Long teamId,
        @Schema(description = "팀 이름", example = "Side Effect") String teamName,
        @Schema(description = "팀 설명", example = "Lg cns am camp 1기 개발 스터디")
                String teamDescription) {}
