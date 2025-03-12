package com.amcamp.domain.rank;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.member.application.MemberService;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.rank.application.RankService;
import com.amcamp.domain.rank.dto.response.BasicRankInfoResponse;
import com.amcamp.domain.rank.dto.response.RankInfoResponse;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.task.application.TaskService;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.TaskDifficulty;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import com.amcamp.global.util.MemberUtil;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class RankServiceTest extends IntegrationTest {
    @Autowired private MemberService memberService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberUtil memberUtil;
    @Autowired private ProjectService projectService;
    @Autowired private SprintService sprintService;
    @Autowired private TaskService taskService;
    @Autowired private RankService rankService;
    @Autowired private TaskRepository taskRepository;

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @BeforeEach
    void setUp() {
        Member member =
                memberRepository.save(
                        Member.createMember(
                                "testNickname",
                                "testProfileImageUrl",
                                OauthInfo.createOauthInfo("testOauthId", "testOauthProvider")));

        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
        String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
        TeamInviteCodeRequest teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
        Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

        ProjectCreateRequest projectRequest =
                new ProjectCreateRequest(
                        teamId,
                        "testProjectTitle",
                        "testProjectGoal",
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 1),
                        "testProjectDescription");

        projectService.createProject(projectRequest);

        SprintCreateRequest sprintRequest =
                new SprintCreateRequest(
                        1L, "1차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
        sprintService.createSprint(sprintRequest);

        // 상 2, 중 3, 하 4
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.HIGH));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.HIGH));

        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID));

        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.LOW));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.LOW));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.LOW));
        taskService.createTask(new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.LOW));

        // 내가 상 2개, 중 1개, 하 1개를 하고, 다른 팀원이 중 2개, 하 3개를 한 상황 가정
        taskService.assignTask(1L);
        taskService.assignTask(2L);
        taskService.assignTask(3L);
        taskService.assignTask(9L);

        taskService.updateTaskToDoInfo(1L);
        taskService.updateTaskToDoInfo(2L);
        taskService.updateTaskToDoInfo(3L);
        taskService.updateTaskToDoInfo(9L);

        // 프로젝트 참여 메소드 부재
    }

    @Nested
    class 개별_기여도_조회_시 {
        @Test
        void 팀_참여자가_아니라면_에러반환() {
            Member newMember = memberRepository.save(Member.createMember("member", null, null));
            loginAs(newMember);

            assertThatThrownBy(() -> rankService.getRankByMember(1L))
                    .isInstanceOf(
                            new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED).getClass())
                    .hasMessage(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트_참여자가_아니라면_에러반환() {
            // 프로젝트 참여 코드 필요
            //
            //
            //			assertThatThrownBy(() -> rankService.getRankByMember(1L))
            //				.isInstanceOf(
            //					new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED)
            //						.getClass())
            //				.hasMessage(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED.getMessage());
        }

        @Test
        void 스프린트가_존재하지_않으면_에러_반환() {
            Member member = memberUtil.getCurrentMember();
            assertThatThrownBy(() -> rankService.getRankByMember(2L))
                    .isInstanceOf(new CommonException(SprintErrorCode.SPRINT_NOT_FOUND).getClass())
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 태스크가_존재하지_않으면_에러_반환() {
            SprintCreateRequest sprintRequest =
                    new SprintCreateRequest(
                            1L, "2차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
            sprintService.createSprint(sprintRequest);
            assertThatThrownBy(() -> rankService.getRankByMember(2L))
                    .isInstanceOf(
                            new CommonException(SprintErrorCode.TASK_NOT_CREATED_YET).getClass())
                    .hasMessage(SprintErrorCode.TASK_NOT_CREATED_YET.getMessage());
        }

        @Test
        void 프로젝트_참여자라면_기여도_반환() {
            Member member = memberUtil.getCurrentMember();
            BasicRankInfoResponse basicRankInfoResponse = rankService.getRankByMember(1L);
            assertThat(basicRankInfoResponse.memberId()).isEqualTo(member.getId());
            assertThat(basicRankInfoResponse.contribution()).isEqualTo(61.0);
        }
    }

    @Nested
    class 프로젝트_기여도_조회_시 {
        @Test
        void 팀_참여자가_아니라면_에러반환() {
            Member newMember = memberRepository.save(Member.createMember("member", null, null));
            loginAs(newMember);

            assertThatThrownBy(() -> rankService.getRankBySprint(1L))
                    .isInstanceOf(
                            new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED).getClass())
                    .hasMessage(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 스프린트가_존재하지_않으면_에러_반환() {
            Member member = memberUtil.getCurrentMember();
            assertThatThrownBy(() -> rankService.getRankByMember(2L))
                    .isInstanceOf(new CommonException(SprintErrorCode.SPRINT_NOT_FOUND).getClass())
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 태스크가_존재하지_않으면_에러_반환() {
            SprintCreateRequest sprintRequest =
                    new SprintCreateRequest(
                            1L, "2차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
            sprintService.createSprint(sprintRequest);
            assertThatThrownBy(() -> rankService.getRankBySprint(2L))
                    .isInstanceOf(
                            new CommonException(SprintErrorCode.TASK_NOT_CREATED_YET).getClass())
                    .hasMessage(SprintErrorCode.TASK_NOT_CREATED_YET.getMessage());
        }

        @Test
        void 팀_참여자라면_기여도_반환() {
            Member member = memberUtil.getCurrentMember();
            List<RankInfoResponse> rankInfoResponse = rankService.getRankBySprint(1L);
            assertThat(rankInfoResponse.get(0).memberId()).isEqualTo(member.getId());
            assertThat(rankInfoResponse.get(0).contribution()).isEqualTo(61.0);
            assertThat(rankInfoResponse.get(0).placement()).isEqualTo(1);
        }
    }
}
