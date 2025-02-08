package com.amcamp.domain.team.api;

import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팀 API", description = "팀 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
	private final TeamService teamService;

	@Operation(summary = "팀 생성", description = "팀을 생성합니다.")
	@PostMapping("/create")
	public TeamInviteCodeResponse teamCreate (@RequestBody TeamCreateRequest teamCreateRequest){
		return teamService.createTeam(teamCreateRequest.teamName(), teamCreateRequest.teamDescription());
	}

	@Operation(summary = "코드 확인", description = "팀 가입을 위한 초대 코드를 확인합니다.")
	@GetMapping("/invite/{teamId}")
	public TeamInviteCodeResponse teamInvite (@PathVariable Long teamId){
		return teamService.generateCode(teamId);
	}

	@Operation(summary = "팀 참가 전 팀 확인", description = "초대 코드를 입력하여 참여하려고 하는 팀 정보를 확인합니다.")
	@PostMapping("/check")
	public TeamInfoResponse teamCheck (@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		return teamService.getTeamInfo(teamInviteCodeRequest.inviteCode());
	}

	@Operation(summary = "팀 참가", description = "팀 정보를 확인 후 팀에 참가합니다.")
	@PostMapping("/join")
	public ResponseEntity<Void> teamJoin (@RequestBody TeamInviteCodeRequest teamInviteCodeRequest){
		teamService.joinTeam(teamInviteCodeRequest.inviteCode());
		return ResponseEntity.ok().build();
	}
}
