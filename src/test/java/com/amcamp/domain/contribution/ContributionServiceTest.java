package com.amcamp.domain.contribution;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.contribution.application.ContributionService;
import com.amcamp.domain.contribution.dto.response.BasicContributionInfoResponse;
import com.amcamp.domain.contribution.dto.response.ContributionInfoResponse;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.task.application.TaskService;
import com.amcamp.domain.task.domain.TaskDifficulty;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ContributionErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
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

public class ContributionServiceTest extends IntegrationTest {
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberUtil memberUtil;
    @Autowired private ProjectService projectService;
    @Autowired private SprintService sprintService;
    @Autowired private TaskService taskService;
    @Autowired private ContributionService contributionService;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private SprintRepository sprintRepository;

    private ProjectParticipant participant;
    private ProjectParticipant newParticipant;
    private Sprint sprint;
    private Project project;
    private Project anotherProject;
    private Member newMember;

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

        newMember = memberRepository.save(Member.createMember("member", null, null));

        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        Team team = teamRepository.save(Team.createTeam("testName", "testDescription"));
        TeamParticipant teamParticipantAdmin =
                teamParticipantRepository.save(
                        TeamParticipant.createParticipant(member, team, TeamParticipantRole.ADMIN));
        TeamParticipant teamParticipantUser =
                teamParticipantRepository.save(
                        TeamParticipant.createParticipant(
                                newMember, team, TeamParticipantRole.USER));

        project =
                projectRepository.save(
                        Project.createProject(
                                team,
                                "testTitle",
                                "testDescription",
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 12, 1)));
        anotherProject =
                projectRepository.save(
                        Project.createProject(
                                team,
                                "testTitle",
                                "testDescription",
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 12, 1)));

        participant =
                projectParticipantRepository.save(
                        ProjectParticipant.createProjectParticipant(
                                teamParticipantAdmin,
                                project,
                                member.getNickname(),
                                member.getProfileImageUrl(),
                                ProjectParticipantRole.ADMIN));
        newParticipant =
                projectParticipantRepository.save(
                        ProjectParticipant.createProjectParticipant(
                                teamParticipantUser,
                                project,
                                newMember.getNickname(),
                                newMember.getProfileImageUrl(),
                                ProjectParticipantRole.MEMBER));

        sprint =
                sprintRepository.save(
                        Sprint.createSprint(
                                project,
                                "1차 스프린트",
                                "아이디어 기획서 제출",
                                LocalDate.of(2026, 2, 1),
                                LocalDate.of(2026, 3, 1)));

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
    }

    @Nested
    class 개별_기여도_조회_시 {
        @Test
        void 팀_참여자가_아니라면_에러반환() {
            Member newTeamMember = memberRepository.save(Member.createMember("member", null, null));
            loginAs(newTeamMember);

            assertThatThrownBy(() -> contributionService.getContributionByMember(1L))
                    .isInstanceOf(
                            new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED).getClass())
                    .hasMessage(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트_참여자가_아니라면_에러반환() {
            loginAs(newMember);
            assertThatThrownBy(() -> contributionService.getContributionByMember(2L))
                    .isInstanceOf(
                            new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED)
                                    .getClass())
                    .hasMessage(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED.getMessage());
        }

        @Test
        void 유효한_프로젝트가_아니라면_에러반환() {
            Member member = memberUtil.getCurrentMember();
            assertThatThrownBy(() -> contributionService.getContributionByMember(3L))
                    .isInstanceOf(
                            new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND).getClass())
                    .hasMessage(ProjectErrorCode.PROJECT_NOT_FOUND.getMessage());
        }

        @Test
        void 태스크가_존재하지_않으면_빈_값_반환() {
            SprintCreateRequest sprintRequest =
                    new SprintCreateRequest(
                            1L, "2차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
            sprintService.createSprint(sprintRequest);

            // when & then
            assertThatThrownBy(() -> contributionService.getContributionByMember(1L))
                    .isInstanceOf(
                            new CommonException(ContributionErrorCode.CONTRIBUTION_NOT_FOUND)
                                    .getClass())
                    .hasMessage(ContributionErrorCode.CONTRIBUTION_NOT_FOUND.getMessage());
        }

        @Test
        void 프로젝트_참여자라면_기여도_반환() {
            Member member = memberUtil.getCurrentMember();
            BasicContributionInfoResponse basicContributionInfoResponse =
                    contributionService.getContributionByMember(1L);
            assertThat(basicContributionInfoResponse.memberId()).isEqualTo(member.getId());
            assertThat(basicContributionInfoResponse.score()).isEqualTo(61.0);
        }
    }

    @Nested
    class 프로젝트_기여도_조회_시 {
        @Test
        void 팀_참여자가_아니라면_에러반환() {
            Member newMember = memberRepository.save(Member.createMember("member", null, null));
            loginAs(newMember);

            assertThatThrownBy(() -> contributionService.getContributionBySprint(1L))
                    .isInstanceOf(
                            new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED).getClass())
                    .hasMessage(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 스프린트가_존재하지_않으면_에러_반환() {
            Member member = memberUtil.getCurrentMember();
            assertThatThrownBy(() -> contributionService.getContributionBySprint(2L))
                    .isInstanceOf(new CommonException(SprintErrorCode.SPRINT_NOT_FOUND).getClass())
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 태스크가_존재하지_않으면_빈_값_반환() {
            SprintCreateRequest sprintRequest =
                    new SprintCreateRequest(
                            1L, "2차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
            sprintService.createSprint(sprintRequest);

            // when
            List<ContributionInfoResponse> contributions =
                    contributionService.getContributionBySprint(2L);

            // then
            assertThat(contributions.isEmpty()).isEqualTo(true);
        }

        @Test
        void 팀_참여자라면_기여도_반환() {
            Member member = memberUtil.getCurrentMember();
            List<ContributionInfoResponse> contributionInfoResponse =
                    contributionService.getContributionBySprint(1L);
            assertThat(contributionInfoResponse.get(0).memberId()).isEqualTo(member.getId());
            assertThat(contributionInfoResponse.get(0).score()).isEqualTo(61.0);
        }
    }
}
