package com.amcamp.domain.participant.dao;

import com.amcamp.domain.member.dto.response.SelectedMemberResponse;
import org.springframework.data.domain.Slice;

public interface ParticipantRepositoryCustom {
    Slice<SelectedMemberResponse> findMemberByTeamExceptMember(
            Long teamId, Long memberId, int pageSize);
}
