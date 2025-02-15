package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> findAllByTeamId(Long teamId);
}
