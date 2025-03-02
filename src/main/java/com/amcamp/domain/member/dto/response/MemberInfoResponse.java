package com.amcamp.domain.member.dto.response;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberRole;
import com.amcamp.domain.member.domain.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberInfoResponse(
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "멤버 닉네임", example = "최현태") String nickname,
        @Schema(description = "멤버 프로필 url", example = "Presigned URL") String profileImageUrl,
        @Schema(description = "멤버 상태", example = "NORMAL") MemberStatus status,
        @Schema(description = "멤버 역할", example = "ROLE_USER") MemberRole role) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getStatus(),
                member.getRole());
    }
}
