package com.amcamp.domain.task.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ToDoInfo;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.dto.request.TaskBasicInfoUpdateRequest;
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

    @Enumerated(EnumType.STRING)
    private SOSStatus sosStatus;

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
            ProjectParticipant assignee,
            SOSStatus sosStatus) {
        this.sprint = sprint;
        this.description = description;
        this.toDoInfo = toDoInfo;
        this.taskDifficulty = taskDifficulty;
        this.assignedStatus = assignedStatus;
        this.assignee = assignee;
        this.sosStatus = sosStatus;
    }

    public static Task createTask(
            Sprint sprint, String description, TaskDifficulty taskDifficulty) {
        return Task.builder()
                .sprint(sprint)
                .description(description)
                .taskDifficulty(taskDifficulty)
                .assignedStatus(AssignedStatus.NOT_ASSIGNED)
                .toDoInfo(ToDoInfo.createToDoInfo(null, null))
                .assignee(null)
                .sosStatus(SOSStatus.NOT_SOS)
                .build();
    }

    public void updateTaskBasicInfo(TaskBasicInfoUpdateRequest request) {
        if (request.description() != null) {
            this.description = request.description();
        }
        if (request.taskDifficulty() != null) {
            this.taskDifficulty = request.taskDifficulty();
        }
    }

    public void updateTaskTodoInfo() {
        this.toDoInfo.updateToDoInfo(
                this.toDoInfo.getStartDt(), LocalDate.now(), ToDoStatus.COMPLETED);
    }

    public void assignTask(ProjectParticipant projectParticipant) {
        this.assignedStatus = AssignedStatus.ASSIGNED;
        this.assignee = projectParticipant;
        this.toDoInfo.updateToDoInfo(LocalDate.now(), null, ToDoStatus.ON_GOING);
    }

    public void updateTaskSOS() {
        if (this.sosStatus != SOSStatus.SOS) {
            this.sosStatus = SOSStatus.SOS;
        } else {
            this.sosStatus = SOSStatus.NOT_SOS;
        }
    }
}
