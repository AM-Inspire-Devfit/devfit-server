package com.amcamp.domain.member.dto.response;

import com.amcamp.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record SelectedMemberResponse(
        @Schema(description = "멤버 아이디", example = "1") Long memberId,
        @Schema(description = "멤버 닉네임", example = "최현태") String nickname,
        @Schema(description = "멤버 프로필 url", example = "Presigned URL") String profileImageUrl) {

    public static SelectedMemberResponse from(Member member) {
        return new SelectedMemberResponse(
                member.getId(), member.getNickname(), member.getProfileImageUrl());
    }
}
