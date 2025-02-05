package com.amcamp.domain.team.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantRole {

	ADMIN("TEAM_ADMIN"),
	USER("TEAM_USER"),
	;

	private final String role;
}
