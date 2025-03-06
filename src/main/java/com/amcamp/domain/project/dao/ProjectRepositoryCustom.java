package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {
    List<Project> findAllByTeamId(Long teamId);

    Slice<ProjectInfoResponse> findAllByTeamIdWithPagination(
            Long teamId, Long lastProjectId, int pageSize, TeamParticipant teamParticipant, boolean isParticipating);
}
