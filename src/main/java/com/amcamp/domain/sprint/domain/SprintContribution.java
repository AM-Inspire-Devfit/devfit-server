package com.amcamp.domain.sprint.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import com.amcamp.domain.project.domain.ProjectParticipant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SprintContribution extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sprint_contribution_id")
	private Long Id;

	@ManyToOne
	private Sprint sprint;

	//기여한 멤버
	@ManyToOne
	@JoinColumn(name = "participant_id")
	private ProjectParticipant participant;

	private Integer placement;
	// 기여도 점수/비율
	private Double contribution;
}
