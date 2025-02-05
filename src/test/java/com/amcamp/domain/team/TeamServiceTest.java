package com.amcamp.domain.team;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberRole;
import com.amcamp.domain.member.domain.MemberStatus;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.Participant;
import com.amcamp.domain.team.domain.ParticipantRole;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.dao.ParticipantRepository;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.AuthErrorCode;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import com.amcamp.global.util.MemberUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.amcamp.domain.member.domain.Member.createMember;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TeamServiceTest {
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ParticipantRepository participantRepository;
	@Autowired
	private TeamService teamService;
	@Autowired
	private MemberUtil memberUtil;

	@Test
	@Transactional
	void 팀_생성_후_참가자를_조회한다() {
		// given
		UserDetails userDetails = new PrincipalDetails(1L, MemberRole.USER);
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		Member savedMember = memberRepository.save(Member.createMember("username", null, null));  // 멤버 저장

		String teamName = "팀 이름";
		String teamDescription = "팀 설명";

		//when
		Team savedTeam = teamService.saveTeam(teamName, teamDescription);

		// then
		Team getTeam = teamRepository.findById(savedTeam.getId())
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

		Assertions.assertNotNull(savedTeam);
		Assertions.assertEquals(teamName, getTeam.getTeam_name());
		Assertions.assertEquals(teamDescription, getTeam.getTeam_description());

		Participant getParticipant = participantRepository.findByMemberAndTeam(savedMember, getTeam)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

		Assertions.assertNotNull(getParticipant);
		Assertions.assertEquals(savedMember.getId(), getParticipant.getMember().getId());
		Assertions.assertEquals(savedTeam.getId(), getParticipant.getTeam().getId());
		Assertions.assertEquals(ParticipantRole.ADMIN, getParticipant.getRole());  // ADMIN 역할 확인
	}

	@Test
	@Transactional
	void 이미_참여한_멤버가_팀에_참여한다(){
		//given
		UserDetails userDetails = new PrincipalDetails(1L, MemberRole.USER);
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		Member savedMember = memberRepository.save(Member.createMember("username", null, null));  // 멤버 저장
		Team savedTeam = teamService.saveTeam("팀 이름", "팀 설명");

		//when & then
		CommonException exception = assertThrows(CommonException.class, () -> teamService.joinTeam(savedTeam.getId()));
		Assertions.assertEquals(TeamErrorCode.ALREADY_PARTICIPANT, exception.getErrorCode());
	}

	@Test
	@Transactional
	void 새롭게_참여하는_멤버가_팀에_참여한다(){
		// given
		// 1. savedAdmin 로그인 처리 후 팀 생성
		Member savedAdmin = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedAdmin.getId(), savedAdmin.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
		Team savedTeam = teamService.saveTeam("팀 이름", "팀 설명");

		// 2. savedMember 로그인 처리 후 팀 참여
		Member savedMember = memberRepository.save(Member.createMember("member", null, null));
		UserDetails newUserDetails = new PrincipalDetails(savedMember.getId(), savedMember.getRole());
		UsernamePasswordAuthenticationToken newToken =
			new UsernamePasswordAuthenticationToken(newUserDetails, null, newUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newToken);

		// when
		teamService.joinTeam(savedTeam.getId());

		// then
		Participant participant = participantRepository.findByMemberAndTeam(savedMember, savedTeam)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

		Assertions.assertEquals(savedMember, participant.getMember());
		Assertions.assertEquals(participant.getRole(), ParticipantRole.USER);
		Assertions.assertEquals(savedTeam, participant.getTeam());
	}
	@Test
	@Transactional
	void 팀이_존재_하지_않는데_팀에_참여(){
		//given
		UserDetails userDetails = new PrincipalDetails(1L, MemberRole.USER);
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		Member savedMember = memberRepository.save(Member.createMember("username", null, null));  // 멤버 저장
		Long nonExistentTeamId = 9999L;

		// when & then
		Assertions.assertEquals(TeamErrorCode.TEAM_NOT_FOUND, assertThrows(CommonException.class, () -> {
			teamService.joinTeam(nonExistentTeamId);}).getErrorCode());
	}
	@Test
	@Transactional
	void 로그인_하지_않은_멤버가_팀에_참여(){
		// given
		Member savedAdmin = memberRepository.save(Member.createMember("admin", null, null));
		UserDetails userDetails = new PrincipalDetails(savedAdmin.getId(), savedAdmin.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
		Team savedTeam = teamService.saveTeam("팀 이름", "팀 설명");

		SecurityContextHolder.getContext().setAuthentication(null);

		// when & then
		Assertions.assertEquals(AuthErrorCode.AUTH_NOT_FOUND, assertThrows(CommonException.class, () -> {
			teamService.joinTeam(savedTeam.getId());}).getErrorCode());
	}
}
