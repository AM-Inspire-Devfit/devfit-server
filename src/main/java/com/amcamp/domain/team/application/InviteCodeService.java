package com.amcamp.domain.team.application;

import com.amcamp.domain.team.domain.Team;

public interface InviteCodeService {
	public String generateCode(Long teamId);
	Team searchTeamByCode(String inviteCode);
}
