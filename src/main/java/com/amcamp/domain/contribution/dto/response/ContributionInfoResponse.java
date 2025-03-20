package com.amcamp.domain.contribution.dto.response;

import com.amcamp.domain.contribution.domain.Contribution;
import io.swagger.v3.oas.annotations.media.Schema;

public record ContributionInfoResponse(
        @Schema(description = "기여도 아이디", example = "1") Long contributionId,
        @Schema(description = "스프린트 아이디", example = "1") Long sprintId,
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "멤버 닉네임", example = "최현태") String nickname,
        @Schema(description = "멤버 프로필 url", example = "Presigned URL") String profileImageUrl,
        @Schema(description = "기여도 점수", example = "68.1") Double score) {
    public static ContributionInfoResponse from(Contribution contribution) {
        return new ContributionInfoResponse(
                contribution.getId(),
                contribution.getSprint().getId(),
                contribution.getParticipant().getId(),
                contribution.getParticipant().getProjectNickname(),
                contribution.getParticipant().getProjectProfile(),
                contribution.getScore());
    }
}
