package com.amcamp.domain.sprint.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ToDoInfo;
import com.amcamp.domain.task.domain.Task;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprint extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sprint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Lob private String description;

    @Embedded private ToDoInfo toDoInfo;

    // Task
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // 기여도
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SprintContribution> contributions = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Sprint(
            Project project,
            String description,
            ToDoInfo toDoInfo,
            List<SprintContribution> contributions,
            List<Task> tasks) {
        this.project = project;
        this.description = description;
        this.toDoInfo = toDoInfo;
        this.contributions = contributions;
        this.tasks = tasks;
    }

    public static Sprint createSprint(
            Project project,
            String description,
            LocalDateTime startDt,
            LocalDateTime dueDt,
            @Nullable List<SprintContribution> contributions,
            @Nullable List<Task> tasks) {
        return Sprint.builder()
                .project(project)
                .description(description)
                .toDoInfo(ToDoInfo.createToDoInfo(startDt, dueDt))
                .contributions(contributions != null ? contributions : new ArrayList<>())
                .tasks(tasks != null ? tasks : new ArrayList<>())
                .build();
    }
}
