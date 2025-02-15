package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import org.springframework.data.domain.Slice;

public interface TeamParticipantRepositoryCustom {

    Slice<BasicMemberResponse> findMemberByTeamExceptAdmin(
            Long teamId, Long memberId, int pageSize, TeamParticipantRole role);
}
