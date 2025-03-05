package com.amcamp.domain.project;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.project.dto.request.ProjectBasicInfoUpdateRequest;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.request.ProjectTodoDateInfoUpdateRequest;
import com.amcamp.domain.project.dto.request.ProjectTodoStatusInfoUpdateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectParticipationInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ProjectServiceTest extends IntegrationTest {
    @Autowired private ProjectService projectService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;
    @Autowired private ProjectRepository projectRepository;

    private Member member;
    private Member anotherMember;

    private LocalDate startDt = LocalDate.of(2026, 1, 1);
    private LocalDate dueDt = LocalDate.of(2026, 12, 1);

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private void logout() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    private void setUp() {
        member =
                Member.createMember(
                        "member",
                        "testProfileImageUrl",
                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        memberRepository.save(member);
        loginAs(member);

        anotherMember =
                Member.createMember(
                        "anotherMember",
                        "testProfileImageUrl",
                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        memberRepository.save(anotherMember);
    }

    @AfterEach
    public void afterEach() {
        logout();
        projectParticipantRepository.deleteAll();
        projectRepository.deleteAll();
        teamParticipantRepository.deleteAll();
        teamRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 프로젝트를_생성하면_정상적으로_저장된다() {
        // given
        TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
        String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
        TeamInviteCodeRequest teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
        Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

        ProjectCreateRequest request =
                new ProjectCreateRequest(
                        teamId,
                        "testProjectTitle",
                        "testProjectGoal",
                        startDt,
                        dueDt,
                        "testProjectDescription");

        // when
        projectService.createProject(request);

        // then
        Project project = projectRepository.findById(1L).get();
        assertThat(project.getId()).isEqualTo(1L);
        assertThat(project)
                .extracting("id", "title", "description", "goal")
                .containsExactlyInAnyOrder(
                        1L, "testProjectTitle", "testProjectDescription", "testProjectGoal");
    }

    @Nested
    class 프로젝트_조회 {
        @Test
        void 팀_ID로_조회하면_전체_프로젝트가_정상적으로_반환된다() {
            // given
            TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
            String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
            TeamInviteCodeRequest teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
            Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

            ProjectCreateRequest request1 =
                    new ProjectCreateRequest(
                            teamId,
                            "project1",
                            "projectGoal",
                            startDt,
                            dueDt,
                            "projectDescription");
            ProjectCreateRequest request2 =
                    new ProjectCreateRequest(
                            teamId,
                            "project1",
                            "projectGoal",
                            startDt,
                            dueDt,
                            "projectDescription");

            projectService.createProject(request1);
            // member logout 후 anotherMember 로그인
            logout();
            loginAs(anotherMember);
            // 팀 참가
            teamService.joinTeam(teamInviteCodeRequest);
            // anotherMember 새 프로젝트 생성
            projectService.createProject(request2);

            // when

            List<ProjectParticipationInfoResponse> foundResponse =
                    projectService.getProjectListInfo(teamId);

            foundResponse.stream()
                    .filter(ProjectParticipationInfoResponse::isParticipate)
                    .forEach(r -> assertThat(r.projectInfo().projectTitle()).isEqualTo("project1"));
            foundResponse.stream()
                    .filter(f -> !f.isParticipate())
                    .forEach(r -> assertThat(r.projectInfo().projectTitle()).isEqualTo("project1"));
        }

        @Test
        void 프로젝트를_ID로_조회하면_정상적으로_반환된다() {
            // given
            TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
            String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
            TeamInviteCodeRequest teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
            Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

            ProjectCreateRequest request =
                    new ProjectCreateRequest(
                            teamId,
                            "testProjectTitle",
                            "testProjectGoal",
                            startDt,
                            dueDt,
                            "testProjectDescription");

            projectService.createProject(request);
            Project project = projectRepository.findById(1L).get();
            // when
            ProjectInfoResponse foundResponse = projectService.getProjectInfo(1L);
            // then

            assertThat(foundResponse)
                    .extracting(
                            "projectId",
                            "projectTitle",
                            "projectDescription",
                            "projectGoal",
                            "startDt",
                            "dueDt")
                    .containsExactlyInAnyOrder(
                            project.getId(),
                            project.getTitle(),
                            project.getDescription(),
                            project.getGoal(),
                            project.getToDoInfo().getStartDt(),
                            project.getToDoInfo().getDueDt());
        }

        @Test
        void ID가_유효하지_않으면_예외가_발생한다() {
            // given
            Long invalidProjectId = Long.MAX_VALUE;
            // when, then
            assertThatThrownBy(() -> projectService.getProjectInfo(invalidProjectId))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(ProjectErrorCode.PROJECT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 프로젝트_업데이트 {
        String originalTitle = "originalProjectTitle";
        String originalGoal = "originalProjectTitle";
        String originalDescription = "originalProjectGoal";
        String updatedTitle = "updatedProjectTitle";
        String updatedGoal = "updatedProjectGoal";
        String updatedDescription = "updatedProjectDescription";
        TeamInviteCodeRequest teamInviteCodeRequest;

        void createTestProject() {
            TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
            String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
            teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
            Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

            ProjectCreateRequest request =
                    new ProjectCreateRequest(
                            teamId,
                            originalTitle,
                            originalGoal,
                            startDt,
                            dueDt,
                            originalDescription);

            projectService.createProject(request);
        }

        @Test
        void 프로젝트_기본정보를_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            projectService.updateProjectBasicInfo(
                    1L,
                    new ProjectBasicInfoUpdateRequest(
                            updatedTitle, updatedGoal, updatedDescription));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getTitle()).isEqualTo(updatedTitle);
            assertThat(updatedProject.getGoal()).isEqualTo(updatedGoal);
            assertThat(updatedProject.getDescription()).isEqualTo(updatedDescription);
        }

        @Test
        void 팀_참여자가_아닌_사용자는_프로젝트_수정이_제한된다() {
            // given
            createTestProject();
            logout();
            // 팀에 속하지 않은 사용자 로그인
            loginAs(anotherMember);

            // when, then
            assertThatThrownBy(
                            () ->
                                    projectService.updateProjectBasicInfo(
                                            1L,
                                            new ProjectBasicInfoUpdateRequest(
                                                    updatedTitle, updatedGoal, updatedDescription)))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트_참여자가_아닌_팀_참가자는_수정이_제한된다() {
            // given
            createTestProject();
            logout();
            // 다른 팀 멤버
            loginAs(anotherMember);
            teamService.joinTeam(teamInviteCodeRequest);

            // when, then
            assertThatThrownBy(
                            () ->
                                    projectService.updateProjectBasicInfo(
                                            1L,
                                            new ProjectBasicInfoUpdateRequest(
                                                    updatedTitle, updatedGoal, updatedDescription)))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(
                            ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트_기본정보를_타이틀만_수정하면_타이틀만_수정된다() {
            // given
            createTestProject();
            // when
            projectService.updateProjectBasicInfo(
                    1L, new ProjectBasicInfoUpdateRequest(updatedTitle, null, null));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getTitle()).isEqualTo(updatedTitle);
            assertThat(updatedProject.getGoal()).isEqualTo(originalGoal);
            assertThat(updatedProject.getDescription()).isEqualTo(originalDescription);
        }

        @Test
        void 프로젝트_기본정보를_목표만_수정하면_목표만_수정된다() {
            // given
            createTestProject();
            // when
            projectService.updateProjectBasicInfo(
                    1L, new ProjectBasicInfoUpdateRequest(null, updatedGoal, null));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getTitle()).isEqualTo(originalTitle);
            assertThat(updatedProject.getGoal()).isEqualTo(updatedGoal);
            assertThat(updatedProject.getDescription()).isEqualTo(originalDescription);
        }

        @Test
        void 프로젝트_기본정보를_상세설명만_수정하면_상세설명만_수정된다() {
            // given
            createTestProject();
            // when
            projectService.updateProjectBasicInfo(
                    1L, new ProjectBasicInfoUpdateRequest(null, null, updatedDescription));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getTitle()).isEqualTo(originalTitle);
            assertThat(updatedProject.getGoal()).isEqualTo(originalGoal);
            assertThat(updatedProject.getDescription()).isEqualTo(updatedDescription);
        }

        @Test
        void 프로젝트_일정정보를_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            LocalDate updatedStartDt = LocalDate.of(2027, 1, 1);
            LocalDate updatedDueDt = LocalDate.of(2027, 12, 1);
            projectService.updateProjectTodoDateInfo(
                    1L, new ProjectTodoDateInfoUpdateRequest(updatedStartDt, updatedDueDt));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getToDoInfo().getStartDt()).isEqualTo(updatedStartDt);
            assertThat(updatedProject.getToDoInfo().getDueDt()).isEqualTo(updatedDueDt);
        }

        @Test
        void 잘못된_날짜를_입력하면_오류가_발생한다() {
            // given
            createTestProject();
            // when
            LocalDate updatedStartDt = LocalDate.of(2027, 1, 1);
            LocalDate updatedDueDt = LocalDate.of(2026, 1, 1);
            ProjectTodoDateInfoUpdateRequest request =
                    new ProjectTodoDateInfoUpdateRequest(updatedStartDt, updatedDueDt);

            // then
            assertThatThrownBy(() -> projectService.updateProjectTodoDateInfo(1L, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(GlobalErrorCode.INVALID_DATE_ERROR.getMessage());
        }

        @Test
        void 프로젝트_진행상태를_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            projectService.updateProjectTodoStatusInfo(
                    1L, new ProjectTodoStatusInfoUpdateRequest(ToDoStatus.COMPLETED));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getToDoInfo().getToDoStatus())
                    .isEqualTo(ToDoStatus.COMPLETED);
        }
    }
}
