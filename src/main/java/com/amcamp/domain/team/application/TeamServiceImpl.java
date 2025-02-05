package com.amcamp.domain.team.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.team.dao.ParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.ParticipantRole;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
	private final MemberUtil memberUtil;
	private final TeamRepository teamRepository;
	private final ParticipantRepository participantRepository;

	@Override
	@Transactional
	public Team saveTeam(String teamName, String teamDescription) {
		Member member = memberUtil.getCurrentMember();

		Team team = Team.createTeam(teamName, teamDescription);
		teamRepository.save(team);

		Participant participant = Participant.createParticipant(member, team, ParticipantRole.ADMIN);
		participantRepository.save(participant);

		return team;
	}

	@Override
	@Transactional
	public Participant joinTeam(Long teamId) {
		Member member = memberUtil.getCurrentMember();
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

		boolean isAlreadyParticipant = participantRepository.existsByMemberAndTeam(member, team);
		if (isAlreadyParticipant) {
			throw new CommonException(TeamErrorCode.ALREADY_PARTICIPANT);
		}

		Participant participant = Participant.createParticipant(member, team, ParticipantRole.USER);
		participantRepository.save(participant);
		return participant;
	}
}
