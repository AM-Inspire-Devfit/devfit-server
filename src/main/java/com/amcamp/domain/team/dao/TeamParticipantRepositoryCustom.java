package com.amcamp.domain.team.dao;

import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import org.springframework.data.domain.Slice;

public interface TeamParticipantRepositoryCustom {
    Slice<BasicMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize);

    Slice<BasicMemberResponse> findMemberByTeamExceptAdmin(
            Long teamId, Long memberId, int pageSize);
}
