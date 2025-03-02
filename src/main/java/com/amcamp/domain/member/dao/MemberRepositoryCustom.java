package com.amcamp.domain.member.dao;

import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import org.springframework.data.domain.Slice;

public interface MemberRepositoryCustom {
    Slice<BasicMemberResponse> findMemberByTeamExceptAdmin(
            Long teamId, Long lastMemberId, int pageSize, TeamParticipantRole role);
}
