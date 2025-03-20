package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.domain.TeamParticipant;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {

    Slice<ProjectInfoResponse> findAllByTeamIdWithPagination(
            Long teamId,
            Long lastProjectId,
            int pageSize,
            TeamParticipant teamParticipant,
            boolean isParticipant);
}
