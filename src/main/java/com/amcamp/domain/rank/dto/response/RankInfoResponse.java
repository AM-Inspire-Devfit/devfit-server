package com.amcamp.domain.rank.dto.response;

import com.amcamp.domain.rank.domain.Rank;
import io.swagger.v3.oas.annotations.media.Schema;

public record RankInfoResponse(
        @Schema(description = "랭크 아이디", example = "1") Long rankId,
        @Schema(description = "스프린트 아이디", example = "1") Long sprintId,
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "멤버 닉네임", example = "최현태") String nickname,
        @Schema(description = "멤버 프로필 url", example = "Presigned URL") String profileImageUrl,
        @Schema(description = "멤버 등위", example = "1") Integer placement,
        @Schema(description = "기여도", example = "68.1") Double contribution) {
    public static RankInfoResponse from(Rank rank) {
        return new RankInfoResponse(
                rank.getId(),
                rank.getSprint().getId(),
                rank.getParticipant().getTeamParticipant().getMember().getId(),
                rank.getParticipant().getTeamParticipant().getMember().getNickname(),
                rank.getParticipant().getTeamParticipant().getMember().getProfileImageUrl(),
                rank.getPlacement(),
                rank.getContribution());
    }
}
