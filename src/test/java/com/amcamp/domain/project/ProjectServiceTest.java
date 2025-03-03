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
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.request.ProjectTextInfoUpdateRequest;
import com.amcamp.domain.project.dto.request.ProjectTodoInfoUpdateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
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
        //        @Test
        //        void 팀_ID로_조회하면_전체_프로젝트가_정상적으로_반환된다() {
        //            // given
        //            TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
        //            String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
        //            TeamInviteCodeRequest teamInviteCodeRequest = new
        // TeamInviteCodeRequest(inviteCode);
        //            Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();
        //
        //            ProjectCreateRequest request1 =
        //                    new ProjectCreateRequest(
        //                            teamId,
        //                            "project1",
        //                            "projectDescription",
        //                            "projectGoal",
        //                            startDt,
        //                            dueDt);
        //            ProjectCreateRequest request2 =
        //                    new ProjectCreateRequest(
        //                            teamId,
        //                            "project2",
        //                            "projectDescription",
        //                            "projectGoal",
        //                            startDt,
        //                            dueDt);
        //
        //            ProjectCreateRequest request3 =
        //                    new ProjectCreateRequest(
        //                            teamId,
        //                            "project3",
        //                            "projectDescription",
        //                            "projectGoal",
        //                            startDt,
        //                            dueDt);
        //
        //            ProjectCreateRequest request4 =
        //                    new ProjectCreateRequest(
        //                            teamId,
        //                            "project4",
        //                            "projectDescription",
        //                            "projectGoal",
        //                            startDt,
        //                            dueDt);
        //
        //            projectService.createProject(request1);
        //            projectService.createProject(request2);
        //            // member logout 후 anotherMember 로그인
        //            logout();
        //            loginAs(anotherMember);
        //            // 팀 참가
        //            teamService.joinTeam(teamInviteCodeRequest);
        //            // anotherMember 새 프로젝트 생성
        //            projectService.createProject(request1);
        //            projectService.createProject(request2);
        //
        //            // when
        //            ProjectListInfoResponse foundResponse =
        // projectService.getProjectListInfo(teamId);

        //        }

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
        void createTestProject() {
            TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
            String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
            TeamInviteCodeRequest teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
            Long teamId = teamService.getTeamByCode(teamInviteCodeRequest).teamId();

            ProjectCreateRequest request =
                    new ProjectCreateRequest(
                            teamId,
                            "originalProjectTitle",
                            "originalProjectGoal",
                            startDt,
                            dueDt,
                            "originalProjectDescription");

            projectService.createProject(request);
        }

        @Test
        void 프로젝트_타이틀을_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            String updatedTitle = "updatedProjectTitle";
            projectService.updateProjectTitle(new ProjectTextInfoUpdateRequest(1L, updatedTitle));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getTitle()).isEqualTo(updatedTitle);
        }

        @Test
        void 프로젝트_목표를_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            String updatedGoal = "updatedProjectGoal";
            projectService.updateProjectGoal(new ProjectTextInfoUpdateRequest(1L, updatedGoal));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getGoal()).isEqualTo(updatedGoal);
        }

        @Test
        void 프로젝트_상세설명을_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            String updatedDescription = "updatedProjectDescription";
            projectService.updateProjectDescription(
                    new ProjectTextInfoUpdateRequest(1L, updatedDescription));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getDescription()).isEqualTo(updatedDescription);
        }

        @Test
        void 프로젝트_상태정보를_수정하면_정상적으로_수정된다() {
            // given
            createTestProject();
            // when
            LocalDate updatedStartDt = LocalDate.of(2027, 1, 1);
            LocalDate updatedDueDt = LocalDate.of(2027, 12, 1);
            projectService.updateProjectTodoInfo(
                    new ProjectTodoInfoUpdateRequest(
                            1L, updatedStartDt, updatedDueDt, ToDoStatus.COMPLETED));
            Project updatedProject = projectRepository.findById(1L).get();
            // then
            assertThat(updatedProject.getToDoInfo().getStartDt()).isEqualTo(updatedStartDt);
            assertThat(updatedProject.getToDoInfo().getDueDt()).isEqualTo(updatedDueDt);
            assertThat(updatedProject.getToDoInfo().getToDoStatus())
                    .isEqualTo(ToDoStatus.COMPLETED);
        }

        @Test
        void 잘못된_날짜정보_입력하면_오류가_발생한다() {
            // given
            createTestProject();
            // when
            LocalDate updatedStartDt = LocalDate.of(2027, 1, 1);
            LocalDate updatedDueDt = LocalDate.of(2026, 1, 1);
            ProjectTodoInfoUpdateRequest request =
                    new ProjectTodoInfoUpdateRequest(
                            1L, updatedStartDt, updatedDueDt, ToDoStatus.COMPLETED);

            // then
            assertThatThrownBy(() -> projectService.updateProjectTodoInfo(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(GlobalErrorCode.INVALID_DATE_ERROR.getMessage());
        }
    }
}
