package com.amcamp.domain.task.dao;

import com.amcamp.domain.task.domain.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    List<Task> findBySprintId(Long sprintId);
}
