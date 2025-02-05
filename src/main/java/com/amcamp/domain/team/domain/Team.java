package com.amcamp.domain.team.domain;

import com.amcamp.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long id;
	private String team_name;
	private String team_description;

	@Builder(access = AccessLevel.PRIVATE)
	private Team(String team_name, String team_description){
		this.team_name = team_name;
		this.team_description = team_description;
	}

	public static Team createTeam (String team_name, String team_description){
		return Team.builder()
			.team_name(team_name)
			.team_description(team_description)
			.build();
	}
}
