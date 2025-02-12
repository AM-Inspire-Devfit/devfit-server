package com.amcamp.domain.team.dto.response;

import com.amcamp.domain.team.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInfoResponse(
        @Schema(description = "팀 아이디", example = "1") Long teamId,
        @Schema(description = "팀 이름", example = "Side Effect") String teamName,
        @Schema(description = "팀 설명", example = "Lg cns am camp 1기 개발 스터디") String teamDescription,
        @Schema(description = "팀 이모지", example = "🍇") String teamEmoji) {

    public static TeamInfoResponse from(Team team) {
        return new TeamInfoResponse(
                team.getId(), team.getName(), team.getDescription(), team.getEmoji());
    }
}
