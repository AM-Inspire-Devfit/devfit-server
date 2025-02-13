package com.amcamp.domain.member.dto.response;

import com.amcamp.domain.member.domain.Member;

public record SelectedMemberResponse(Long memberId, String nickname, String profileImageUrl) {

    public static SelectedMemberResponse from(Member member) {
        return new SelectedMemberResponse(
                member.getId(), member.getNickname(), member.getProfileImageUrl());
    }
}
