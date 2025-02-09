package com.amcamp.domain.team.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.participant.dao.ParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.participant.domain.ParticipantRole;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import com.amcamp.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amcamp.global.common.constants.RedisConstants
import java.util.Optional;
import static com.amcamp.global.common.constants.RedisConstants.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService{
	private final MemberUtil memberUtil;
	private final TeamRepository teamRepository;
	private final ParticipantRepository participantRepository;
	private final RedisUtil redisUtil;

	public TeamInviteCodeResponse createTeam(String teamName, String teamDescription) {
		Member member = memberUtil.getCurrentMember();

		Team team = Team.createTeam(teamName, teamDescription);
		teamRepository.save(team);

		Participant participant = Participant.createParticipant(member, team, ParticipantRole.ADMIN);
		participantRepository.save(participant);

		String code = generateCode(team.getId());

		return new TeamInviteCodeResponse(code);
	}


	public TeamInviteCodeResponse getTeamCode (Long teamId) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

		String code = generateCode(team.getId());

		return new TeamInviteCodeResponse(code);
	}

	public TeamInfoResponse getTeamInfo (String inviteCode){
		Team team = searchTeamByCode(inviteCode);
		return new TeamInfoResponse(team.getId(), team.getTeamName());
	}


	public void joinTeam (String inviteCode) {
		Member member = memberUtil.getCurrentMember();
		Team team = searchTeamByCode(inviteCode);
		validateTeamJoin(member, team);

		Participant participant = Participant.createParticipant(member, team, ParticipantRole.USER);
		participantRepository.save(participant);

	}

	private String generateCode(Long teamId) {
		final Optional<String> existingCode = redisUtil.getData(TEAM_ID_PREFIX.formatted(teamId));

		if (existingCode.isEmpty()) {
			String inviteCode = codeGenerator(6);
			redisUtil.setDataExpire(TEAM_ID_PREFIX.formatted(teamId), inviteCode, expirationTime);
			redisUtil.setDataExpire(INVITE_CODE_PREFIX.formatted(inviteCode), teamId.toString(), expirationTime);
			return inviteCode;
		}
		return existingCode.get();
	}

	private Team searchTeamByCode(String inviteCode){
		Long teamId = redisUtil.getData(INVITE_CODE_PREFIX.formatted(inviteCode))
			.map(Long::valueOf)
			.orElseThrow(() -> new CommonException(TeamErrorCode.INVALID_INVITE_CODE));

		return teamRepository.findById(teamId).orElseThrow(() ->
			new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
	}

	private void validateTeamJoin(Member member, Team team) {

		if (participantRepository.findByMemberAndTeam(member, team).isPresent()) {
			throw new CommonException(TeamErrorCode.MEMBER_ALREADY_JOINED);
		}
	}

	private static String codeGenerator(int length){
		byte[] randomBytes = new byte[length];
		secureRandom.nextBytes(randomBytes);
		return base64Encoder.encodeToString(randomBytes);
	}

}
