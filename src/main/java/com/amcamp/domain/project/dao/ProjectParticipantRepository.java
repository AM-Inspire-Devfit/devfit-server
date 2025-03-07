package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {
    Optional<ProjectParticipant> findByProjectAndTeamParticipant(
            Project project, TeamParticipant teamParticipant);

    List<ProjectParticipant> findAllByProject(Project project);

    boolean existsByProjectAndProjectRoleNot(Project project, ProjectParticipantRole role);

    void deleteAllByProject(Project project);
}
