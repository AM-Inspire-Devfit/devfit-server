package com.amcamp.domain.rank.dao;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.rank.domain.Rank;
import com.amcamp.domain.sprint.domain.Sprint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Rank findBySprintAndParticipant(Sprint sprint, ProjectParticipant participant);

    //	@Query("SELECT r FROM Rank r WHERE r.sprint = :sprint ORDER BY r.contribution DESC")
    List<Rank> findBySprintOrderByContributionDesc(Sprint sprint);
}
