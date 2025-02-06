package com.amcamp.domain.team.application;

import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.Team;
import org.springframework.transaction.annotation.Transactional;

public interface TeamService {
	@Transactional
	Team saveTeam(String teamName, String teamDescription);
//	@Transactional
//	Participant joinTeam(Long TeamId);
	@Transactional
	Participant joinTeam(String inviteCode);
}
