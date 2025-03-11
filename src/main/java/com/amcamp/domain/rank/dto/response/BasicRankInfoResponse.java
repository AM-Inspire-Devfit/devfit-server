package com.amcamp.domain.rank.dto.response;

import com.amcamp.domain.rank.domain.Rank;
import io.swagger.v3.oas.annotations.media.Schema;

public record BasicRankInfoResponse(
        @Schema(description = "랭크 아이디", example = "1") Long rankId,
        @Schema(description = "스프린트 아이디", example = "1") Long sprintId,
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "기여도", example = "68.1") Double Contribution) {

    public static BasicRankInfoResponse from(Rank rank) {
        return new BasicRankInfoResponse(
                rank.getId(),
                rank.getSprint().getId(),
                rank.getParticipant().getTeamParticipant().getMember().getId(),
                rank.getContribution());
    }
}
