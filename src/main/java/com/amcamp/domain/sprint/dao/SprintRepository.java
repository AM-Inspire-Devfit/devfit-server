package com.amcamp.domain.sprint.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.sprint.domain.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    @Query("SELECT COUNT(s) FROM Sprint s WHERE s.project = :project")
    long countByProject(@Param("project") Project project);
}
