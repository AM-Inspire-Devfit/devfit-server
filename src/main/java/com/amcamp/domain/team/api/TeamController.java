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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팀 API", description = "팀 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
	private final TeamService teamService;
	private final InviteCodeService inviteCodeService;

	@Operation(summary = "팀 생성", description = "팀을 생성합니다.")
	@PostMapping("/create")
	public TeamCreatedResponse createTeam (@RequestBody TeamCreateRequest teamCreateRequest){
		Team team = teamService.saveTeam(teamCreateRequest.teamName(), teamCreateRequest.teamDescription());
		String inviteCode = inviteCodeService.generateCode(team.getId());
		TeamCreatedResponse response = new TeamCreatedResponse(team, inviteCode);
		return response;
	}
	@Operation(summary = "팀 초대 코드 발급", description = "팀 가입을 위한 초대 코드를 발급합니다.")
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
	@Operation(summary = "팀 참가 전 팀 확인", description = "초대 코드를 입력하여 참여하려고 하는 팀 정보를 확인합니다.")
	@GetMapping("/join")
	public Team checkTeamInfo(@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		Team team = inviteCodeService.searchTeamByCode(teamInviteCodeRequest.inviteCode());
		return team;
	}
	@Operation(summary = "팀 참가", description = "팀 정보를 확인 후 팀에 참가합니다.")
	@PostMapping("/join")
	public Participant joinTeam (@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		Participant participant = teamService.joinTeam(teamInviteCodeRequest.inviteCode());
		return participant;
	}
}
