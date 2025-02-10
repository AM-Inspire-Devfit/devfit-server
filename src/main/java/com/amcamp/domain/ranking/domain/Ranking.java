package com.amcamp.domain.ranking.domain;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.domain.Sprint;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ranking_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sprint_id")
	private Sprint sprint;

	@ManyToOne(fetch = FetchType.EAGER)
	private ProjectParticipant participant;

	//순위

	//기여도 비율
	private Double contribution;


}
