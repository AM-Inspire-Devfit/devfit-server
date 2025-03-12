package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectRegistration;
import com.amcamp.domain.team.domain.TeamParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {
    List<ProjectRegistration> findAllByProject(Project project);

    Optional<ProjectRegistration> findByRequester(TeamParticipant teamParticipant);
}
