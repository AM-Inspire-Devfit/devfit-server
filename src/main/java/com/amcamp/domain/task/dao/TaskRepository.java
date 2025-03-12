package com.amcamp.domain.task.dao;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.domain.Task;
import java.util.List;

import com.amcamp.domain.task.domain.TaskDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    List<Task> findBySprintId(Long sprintId);

    List<Task> findBySprintIdAndAssignee(Long sprintId, ProjectParticipant assignee);

    int countBySprintAndTaskDifficulty(Sprint sprint, TaskDifficulty taskDifficulty);

    int countBySprintAndAssigneeAndTaskDifficulty(
            Sprint sprint, ProjectParticipant assignee, TaskDifficulty taskDifficulty);

    int countBySprint(Sprint sprint);

    @Query(
            "SELECT COUNT(t) FROM Task t WHERE t.sprint = :sprint AND t.toDoInfo.toDoStatus = :toDoStatus")
    Double countBySprintAndTodoStatus(Sprint sprint, ToDoStatus toDoStatus);
}
