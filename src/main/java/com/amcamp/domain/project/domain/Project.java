package com.amcamp.domain.project.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long id;
	//팀
	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	//프로젝트 이름
	@Column(name = "project_title")
	private String title;
	//설명
	@Lob
	private String description;

	//프로젝트 목표
	@Lob
	private String goal;

	@Embedded
	private ToDoInfo toDoInfo;

	//캘린더
	@Builder(access = AccessLevel.PRIVATE)
	private Project(
		Team team, String title, String description, String goal, ToDoInfo toDoInfo){
		this.team = team;
		this.title = title;
		this.description = description;
		this.goal = goal;
		this.toDoInfo = toDoInfo;

	}

	public static Project createProject(Team team, String title, String description, String goal, ToDoInfo toDoInfo){
		return Project.builder()
			.team(team)
			.title(title)
			.description(description)
			.goal(goal)
			.toDoInfo(toDoInfo)
			.build();
	}

}
