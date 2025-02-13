package com.amcamp.domain.project.dao;

import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {
    Optional<ProjectParticipant> findByProjectAndParticipant(
            Project project, Participant participant);
}
