package com.amcamp.domain.contribution.dto.response;

import com.amcamp.domain.contribution.domain.Contribution;
import io.swagger.v3.oas.annotations.media.Schema;

public record BasicContributionInfoResponse(
        @Schema(description = "기여도 아이디", example = "1") Long contributionId,
        @Schema(description = "스프린트 아이디", example = "1") Long sprintId,
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "기여도 점수", example = "68.1") Double score) {

    public static BasicContributionInfoResponse from(Contribution contribution) {
        return new BasicContributionInfoResponse(
                contribution.getId(),
                contribution.getSprint().getId(),
                contribution.getParticipant().getId(),
                contribution.getScore());
    }
}
