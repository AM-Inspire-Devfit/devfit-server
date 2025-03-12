package com.amcamp.domain.project.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.team.domain.Team;
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

    //    // 프로젝트 목표
    //    @Lob private String goal;

    @Embedded private ToDoInfo toDoInfo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();

    // 캘린더
    @Builder(access = AccessLevel.PRIVATE)
    private Project(Team team, String title, String description, ToDoInfo toDoInfo) {
        this.team = team;
        this.title = title;
        this.description = description;
        this.toDoInfo = toDoInfo;
    }

    public static Project createProject(
            Team team, String title, String description, LocalDate startDt, LocalDate dueDt) {
        return Project.builder()
                .team(team)
                .title(title)
                .description(description)
                .toDoInfo(ToDoInfo.createToDoInfo(startDt, dueDt))
                .build();
    }

    public void updateBasic(String title, String description) {
        this.title = (title != null) ? title : this.title;
        this.description = (description != null) ? description : this.description;
    }
}
