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
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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

    private LocalDateTime startDt = LocalDateTime.of(2026, 1, 1, 1, 00);
    private LocalDateTime dueDt = LocalDateTime.of(2026, 12, 1, 1, 00);

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
}
