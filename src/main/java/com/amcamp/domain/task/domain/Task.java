package com.amcamp.domain.task.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ToDoInfo;
import com.amcamp.domain.ranking.domain.Ranking;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.sprint.domain.SprintContribution;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sprint_id")
	private Sprint sprint;

	@Embedded
	private ToDoInfo toDoInfo;

	@Enumerated(EnumType.STRING)
	private TaskDifficulty taskDifficulty;

	@Enumerated(EnumType.STRING)
	private AssignedStatus assignedStatus;

	//태스크 수행 멤버
	@ManyToOne
	@JoinColumn(name = "assignee_id")
	private ProjectParticipant assignee;

}
