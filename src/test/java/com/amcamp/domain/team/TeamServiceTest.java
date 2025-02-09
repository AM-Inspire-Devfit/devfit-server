package com.amcamp.domain.team;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.participant.dao.ParticipantRepository;
import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.participant.domain.ParticipantRole;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.dto.response.TeamInfoResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TeamServiceTest {
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ParticipantRepository participantRepository;
	@Autowired
	private TeamService teamService;


	@Test
	void 팀생성시_초대코드_반환 () {
		// given
		Member savedMember = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		// when
		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");

		// then
		assertNotNull(inviteCodeResponse);
		assertNotNull(inviteCodeResponse.inviteCode());
		Assertions.assertTrue(inviteCodeResponse.inviteCode().length() == 8);
	}

	@Test
	void 팀아이디로_코드확인시_팀이_유효한_경우_초대코드_반환 (){
		// given
		Member savedMember = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
		Long teamId = teamService.getTeamInfo(inviteCodeResponse.inviteCode()).teamId();

		// when
		TeamInviteCodeResponse response = teamService.getTeamCode(teamId);

		// then
		assertNotNull(response);
		assertNotNull(response.inviteCode());
		assertEquals(8, response.inviteCode().length());
	}

	@Test
	void 팀아이디로_코드확인시_팀이_유효하지않는_경우(){
		// given
		Member savedMember = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");

		// when
		Long invalidTeamId = -999L;
		CommonException exception = assertThrows(CommonException.class, () -> teamService.getTeamCode(invalidTeamId));

		// then
		assertEquals(TeamErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());

	}

	@Test
	void 팀참가시_이미_팀에_참가한_경우(){
		//given
		Member savedMember = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
		String inviteCode = inviteCodeResponse.inviteCode();

		// when & then
		CommonException exception = assertThrows(CommonException.class, () -> {
			teamService.joinTeam(inviteCode); // 다시 참여 시도
		});
		assertEquals(TeamErrorCode.MEMBER_ALREADY_JOINED, exception.getErrorCode());
	}

	@Test
	@Transactional
	void 팀참가시_새롭게_참여하는_경우(){
		//given
		// 1. savedAdmin 로그인 후 팀 생성
		Member savedAdmin = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedAdmin.getId(), savedAdmin.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
		String inviteCode = inviteCodeResponse.inviteCode();

		// 2. savedMember 로그인 처리 후 팀 참여
		Member savedMember = memberRepository.save(Member.createMember("member", null, null));
		UserDetails newUserDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken newToken =
			new UsernamePasswordAuthenticationToken(newUserDetails, null, newUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newToken);

		// when
		teamService.joinTeam(inviteCode);

		// then
		TeamInfoResponse teamInfoResponse = teamService.getTeamInfo(inviteCode);
		Team savedTeam = teamRepository.findById(teamInfoResponse.teamId()).orElseThrow(()
			-> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));


		Participant participant = participantRepository.findByMemberAndTeam(savedMember, savedTeam)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

		assertEquals(savedMember, participant.getMember());
		assertEquals(ParticipantRole.USER, participant.getRole());
		assertEquals(savedTeam, participant.getTeam());

	}
	@Test
	void 초대코드로_팀_확인시_코드가_유효한_경우(){
		//given
		Member savedAdmin = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedAdmin.getId(), savedAdmin.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
		String validInviteCode = inviteCodeResponse.inviteCode();

		// when
		TeamInfoResponse teamInfoResponse = teamService.getTeamInfo(validInviteCode);

		// then
		assertNotNull(teamInfoResponse);
		assertNotNull(teamInfoResponse.teamId());

		Team savedTeam = teamRepository.findById(teamInfoResponse.teamId())
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

		assertEquals(savedTeam.getTeamName(), teamInfoResponse.teamName());
		assertEquals(savedTeam.getId(), teamInfoResponse.teamId());  // 반환된 team값과 실제 팀 레파지토리에 저장됨 값이 일치하는지 확인
	}

	@Test
	void 초대코드로_팀_확인시_코드가_유효하지않는_경우(){
		// given
		String invalidInviteCode = "invalidCode";

		// when & then
		CommonException exception = assertThrows(CommonException.class, () -> {
			teamService.getTeamInfo(invalidInviteCode);
		});
		assertEquals(TeamErrorCode.INVALID_INVITE_CODE, exception.getErrorCode());
	}
}
