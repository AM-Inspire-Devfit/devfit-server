package com.amcamp.domain.sprint.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.contribution.domain.Contribution;
import com.amcamp.domain.meeting.domain.Meeting;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ToDoInfo;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.task.domain.Task;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String title;

    @Lob private String goal;

    @Embedded private ToDoInfo toDoInfo;

    // Task
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // Meeting
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meeting> meetings = new ArrayList<>();

    // 기여도
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contribution> contribution = new ArrayList<>();

    // 진척도
    private Double progress;

    @Builder(access = AccessLevel.PRIVATE)
    private Sprint(Project project, String title, String goal, ToDoInfo toDoInfo, Double progress) {
        this.project = project;
        this.title = title;
        this.goal = goal;
        this.toDoInfo = toDoInfo;
        this.progress = progress;
    }

    public static Sprint createSprint(Project project, String title, String goal, LocalDate dueDt) {
        return Sprint.builder()
                .project(project)
                .title(title)
                .goal(goal)
                .progress(0.0)
                .toDoInfo(ToDoInfo.createToDoInfo(dueDt))
                .build();
    }

    public void updateSprintBasic(String goal) {
        if (goal != null) this.goal = goal;
    }

    public void updateSprintToDo(LocalDate startDt, LocalDate dueDt, ToDoStatus status) {
        toDoInfo.updateToDoInfo(startDt, dueDt, status);
    }

    public void updateSprintTitle(String title) {
        this.title = title;
    }

    public void updateProgress(Double progress) {
        this.progress = progress;
    }
}
