package com.amcamp.domain.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class TeamServiceTest {
    @Autowired private TeamRepository teamRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ParticipantRepository participantRepository;
    @Autowired private TeamService teamService;

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Nested
    class 팀_생성_시 {
        @Test
        void 초대코드를_반환한다() {
            // given
            Member member =
                    Member.createMember(
                            "testNickName",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
            memberRepository.save(member);
            loginAs(member);

            // when
            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");

            // then
            assertThat(inviteCodeResponse).isNotNull();
            assertThat(inviteCodeResponse.inviteCode()).isNotNull();
            assertThat(inviteCodeResponse.inviteCode()).hasSize(8);
        }
    }

    @Nested
    class 팀_아이디로_코드확인_시 {
        @Test
        void 팀이_유효한_경우에는_초대코드를_반환한다() {
            // given
            Member member =
                    Member.createMember(
                            "testNickName",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
            memberRepository.save(member);
            loginAs(member);

            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
            Long teamId = teamService.getTeamInfo(inviteCodeResponse.inviteCode()).teamId();

            // when
            TeamInviteCodeResponse response = teamService.getTeamCode(teamId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.inviteCode()).isNotNull();
            assertThat(response.inviteCode()).hasSize(8);
        }

        @Test
        void 팀이_유효하지않는_경우에는_예외가_발생한다() {
            // given
            Member member =
                    Member.createMember(
                            "testNickName",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
            memberRepository.save(member);
            loginAs(member);

            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");

            // when & then
            assertThatThrownBy(() -> teamService.getTeamCode(-999L))
                    .isInstanceOf(CommonException.class)
                    .extracting("errorCode")
                    .isEqualTo(TeamErrorCode.TEAM_NOT_FOUND);
        }
    }

    @Nested
    class 팀_참가_시 {
        @Test
        @Transactional
        void 새롭게_참여하는_경우에는_팀에_USER로_등록된다() {
            // given
            // 1. savedAdmin 로그인 후 팀 생성
            Member savedAdmin =
                    Member.createMember(
                            "admin",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("adminOauthId", "adminOauthProvider"));
            memberRepository.save(savedAdmin);
            loginAs(savedAdmin);

            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
            String inviteCode = inviteCodeResponse.inviteCode();

            // 2. savedMember 로그인 처리 후 팀 참여
            Member savedMember = memberRepository.save(Member.createMember("member", null, null));
            //			UserDetails newUserDetails = new PrincipalDetails(savedMember.getId(),
            // savedMember.getRole());
            //			UsernamePasswordAuthenticationToken newToken =
            //				new UsernamePasswordAuthenticationToken(newUserDetails, null,
            // newUserDetails.getAuthorities());
            //			SecurityContextHolder.getContext().setAuthentication(newToken);
            loginAs(savedMember);

            // when
            teamService.joinTeam(inviteCode);

            // then
            TeamInfoResponse teamInfoResponse = teamService.getTeamInfo(inviteCode);
            Team savedTeam =
                    teamRepository
                            .findById(teamInfoResponse.teamId())
                            .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

            Participant participant =
                    participantRepository
                            .findByMemberAndTeam(savedMember, savedTeam)
                            .orElseThrow(
                                    () ->
                                            new CommonException(
                                                    TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

            assertThat(participant.getMember()).isEqualTo(savedMember);
            assertThat(participant.getRole()).isEqualTo(ParticipantRole.USER);
            assertThat(participant.getTeam()).isEqualTo(savedTeam);
        }

        @Test
        void 이미_팀에_참가한_경우에는_예외가_발생한다() {
            // given
            Member member =
                    Member.createMember(
                            "testNickName",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
            memberRepository.save(member);
            loginAs(member);

            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
            String inviteCode = inviteCodeResponse.inviteCode();

            // when & then
            assertThatThrownBy(() -> teamService.joinTeam(inviteCode))
                    .isInstanceOf(CommonException.class)
                    .extracting("errorCode")
                    .isEqualTo(TeamErrorCode.MEMBER_ALREADY_JOINED);
        }
    }

    @Nested
    class 초대코드로_팀_확인_시 {
        @Test
        void 코드가_유효한_경우에는_팀_정보를_반환한다() {
            // given
            Member member =
                    Member.createMember(
                            "testNickName",
                            "testProfileImageUrl",
                            OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
            memberRepository.save(member);
            loginAs(member);

            TeamInviteCodeResponse inviteCodeResponse = teamService.createTeam("팀 이름", "팀 설명");
            String validInviteCode = inviteCodeResponse.inviteCode();

            // when
            TeamInfoResponse teamInfoResponse = teamService.getTeamInfo(validInviteCode);

            // then
            assertThat(teamInfoResponse).isNotNull();
            assertThat(teamInfoResponse.teamId()).isNotNull();

            Team savedTeam =
                    teamRepository
                            .findById(teamInfoResponse.teamId())
                            .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

            assertThat(teamInfoResponse.teamName()).isEqualTo(savedTeam.getTeamName());
            assertThat(teamInfoResponse.teamId()).isEqualTo(savedTeam.getId());
        }

        @Test
        void 코드가_유효하지않는_경우에는_예외를_반환한다() {
            // given
            String invalidInviteCode = "invalidCode";

            // when & then
            assertThatThrownBy(() -> teamService.getTeamInfo(invalidInviteCode))
                    .isInstanceOf(CommonException.class)
                    .extracting("errorCode")
                    .isEqualTo(TeamErrorCode.INVALID_INVITE_CODE);
        }
    }
}
