package com.amcamp.domain.team.api;

import com.amcamp.domain.auth.dto.response.SocialLoginResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

	private final TeamService teamService;

	@PostMapping("/create")
	public CommonResponse<Team> createTeam (@RequestBody TeamCreateRequest teamCreateRequest){
		Team team = teamService.saveTeam(teamCreateRequest.teamName(), teamCreateRequest.teamDescription());
		return CommonResponse.onSuccess(HttpStatus.OK.value(),team);
	}

	@PostMapping("/participate/{teamId}")
	public CommonResponse<Participant> participateTeam (@PathVariable Long teamId){
		Participant participant = teamService.joinTeam(teamId);
		return CommonResponse.onSuccess(HttpStatus.OK.value(),participant);
	}
}
