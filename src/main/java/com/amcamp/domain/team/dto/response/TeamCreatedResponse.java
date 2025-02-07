package com.amcamp.domain.team.dto.response;

import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.Team;

public record TeamCreatedResponse(Team team, String inviteCode) {
}
