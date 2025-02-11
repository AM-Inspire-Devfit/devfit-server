package com.amcamp.domain.project.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.team.domain.Team;
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
public class Project extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 프로젝트 이름
    @Column(name = "project_title")
    private String title;

    // 설명
    @Lob private String description;

    // 프로젝트 목표
    @Lob private String goal;

    @Embedded private ToDoInfo toDoInfo;

    @Nullable
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();

    // 캘린더
    @Builder(access = AccessLevel.PRIVATE)
    private Project(
            Team team,
            String title,
            String description,
            String goal,
            ToDoInfo toDoInfo,
            List<Sprint> sprints) {
        this.team = team;
        this.title = title;
        this.description = description;
        this.goal = goal;
        this.toDoInfo = toDoInfo;
        this.sprints = sprints;
    }

    public static Project createProject(
            Team team,
            String title,
            String description,
            String goal,
            LocalDateTime startDt,
            LocalDateTime dueDt,
            @Nullable List<Sprint> sprints) {
        return Project.builder()
                .team(team)
                .title(title)
                .description(description)
                .goal(goal)
                .toDoInfo(ToDoInfo.createToDoInfo(startDt, dueDt))
                .sprints(sprints != null ? sprints : new ArrayList<>())
                .build();
    }
}
