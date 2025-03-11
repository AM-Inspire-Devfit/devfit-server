package com.amcamp.domain.task.dao;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.sprint.domain.Sprint;
import java.util.List;

import com.amcamp.domain.task.domain.TaskDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    List<Task> findBySprintId(Long sprintId);

    List<Task> findBySprintIdAndAssignee(Long sprintId, ProjectParticipant assignee);

    int countBySprintAndTaskDifficulty(Sprint sprint, TaskDifficulty taskDifficulty);

    int countBySprintAndTaskDifficultyAndAssignee(
            Sprint sprint, ProjectParticipant assginee, TaskDifficulty taskDifficulty);
}
