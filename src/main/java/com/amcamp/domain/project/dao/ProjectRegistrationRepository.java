package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectRegistration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {
    List<ProjectRegistration> findAllByProject(Project project);
}
