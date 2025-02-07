package com.amcamp.domain.team.api;

import com.amcamp.domain.team.application.InviteCodeService;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.response.TeamCreatedResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
	private final TeamService teamService;
	private final InviteCodeService inviteCodeService;

	@PostMapping("/create")
	public TeamCreatedResponse createTeam (@RequestBody TeamCreateRequest teamCreateRequest){
		Team team = teamService.saveTeam(teamCreateRequest.teamName(), teamCreateRequest.teamDescription());
		String inviteCode = inviteCodeService.generateCode(team.getId());
		TeamCreatedResponse response = new TeamCreatedResponse(team, inviteCode);
		return response;
	}
	@PostMapping("/invite/{teamId}")
	public TeamInviteCodeResponse inviteTeam (@PathVariable Long teamId){
		String inviteCode = inviteCodeService.generateCode(teamId);
		TeamInviteCodeResponse response = new TeamInviteCodeResponse(inviteCode);
		return response;
	}
//	@PostMapping("/join/{teamId}")
//	public CommonResponse<Participant> joinTeam (@PathVariable Long teamId){
//		Participant participant = teamService.joinTeam(teamId);
//		return CommonResponse.onSuccess(HttpStatus.OK.value(),participant);
//	}
	@GetMapping("/join")
	public Team checkTeamInfo(@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		Team team = inviteCodeService.searchTeamByCode(teamInviteCodeRequest.inviteCode());
		return team;
	}
	@PostMapping("/join")
	public Participant joinTeam (@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		Participant participant = teamService.joinTeam(teamInviteCodeRequest.inviteCode());
		return participant;
	}
}
