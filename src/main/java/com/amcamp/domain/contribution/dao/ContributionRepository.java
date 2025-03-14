package com.amcamp.domain.contribution.dao;

import com.amcamp.domain.contribution.domain.Contribution;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.domain.Sprint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    Contribution findBySprintAndParticipant(Sprint sprint, ProjectParticipant participant);

    //	@Query("SELECT r FROM Contribution r WHERE r.sprint = :sprint ORDER BY r.score DESC")
    List<Contribution> findBySprintOrderByScoreDesc(Sprint sprint);
}
