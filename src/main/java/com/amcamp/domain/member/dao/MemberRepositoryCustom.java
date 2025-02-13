package com.amcamp.domain.member.dao;

import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
import org.springframework.data.domain.Slice;

public interface MemberRepositoryCustom {
    Slice<SelectedMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize);
}
