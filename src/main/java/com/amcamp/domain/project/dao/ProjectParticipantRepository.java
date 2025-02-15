package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {
    Optional<ProjectParticipant> findByProjectAndTeamParticipant(
            Project project, TeamParticipant teamParticipant);
}
