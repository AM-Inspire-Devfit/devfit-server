package com.amcamp.domain.task.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ToDoInfo;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.dto.request.TaskInfoUpdateRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @Lob private String description;

    @Embedded private ToDoInfo toDoInfo;

    @Enumerated(EnumType.STRING)
    private TaskDifficulty taskDifficulty;

    @Enumerated(EnumType.STRING)
    private AssignedStatus assignedStatus;

    // 태스크 수행 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private ProjectParticipant assignee;

    @Builder(access = AccessLevel.PRIVATE)
    private Task(
            Sprint sprint,
            String description,
            TaskDifficulty taskDifficulty,
            ToDoInfo toDoInfo,
            AssignedStatus assignedStatus,
            ProjectParticipant assignee) {
        this.sprint = sprint;
        this.description = description;
        this.toDoInfo = toDoInfo;
        this.taskDifficulty = taskDifficulty;
        this.assignedStatus = assignedStatus;
        this.assignee = assignee;
    }

    public static Task createTask(
            Sprint sprint,
            String description,
            LocalDate startDt,
            LocalDate dueDt,
            TaskDifficulty taskDifficulty) {
        return Task.builder()
                .sprint(sprint)
                .description(description)
                .taskDifficulty(taskDifficulty)
                .toDoInfo(ToDoInfo.createToDoInfo(startDt, dueDt))
                .assignedStatus(AssignedStatus.NOT_ASSIGNED)
                .assignee(null)
                .build();
    }

    public void updateTask(TaskInfoUpdateRequest request) {
        if (request.description() != null) {
            this.description = request.description();
        }
        if (request.taskDifficulty() != null) {
            this.taskDifficulty = request.taskDifficulty();
        }
        if (request.startDt() != null && request.dueDt() != null) {
            this.toDoInfo.updateToDoInfo(
                    request.startDt(), request.dueDt(), this.getToDoInfo().getToDoStatus());
        }
    }
}
