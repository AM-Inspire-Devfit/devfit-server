package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
import org.springframework.data.domain.Slice;

public interface TeamParticipantRepositoryCustom {
    Slice<SelectedMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize);
}
