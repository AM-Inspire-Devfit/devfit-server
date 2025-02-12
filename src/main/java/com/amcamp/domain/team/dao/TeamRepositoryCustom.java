package com.amcamp.domain.team.dao;

import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import org.springframework.data.domain.Slice;

public interface TeamRepositoryCustom {
    Slice<TeamInfoResponse> findAllTeamByMemberId(Long memberId, Long lastTeamId, int pageSize);
}
