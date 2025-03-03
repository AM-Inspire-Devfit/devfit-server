package com.amcamp.domain.task.dao;

import com.amcamp.domain.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {}
