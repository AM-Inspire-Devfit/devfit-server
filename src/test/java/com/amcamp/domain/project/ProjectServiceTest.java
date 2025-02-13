package com.amcamp.domain.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectListInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectServiceTest {
    @Autowired private ProjectService projectService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
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

    @Test
    void 프로젝트를_생성하면_정상적으로_저장된다() {
        Long teamId =
                teamService
                        .getTeamByCode(teamService.createTeam("팀 이름", "팀 설명").inviteCode())
                        .teamId();

        ProjectCreateRequest request =
                new ProjectCreateRequest(
                        teamId,
                        "projectTitle",
                        "projectDescription",
                        "projectGoal",
                        startDt,
                        dueDt);

        ProjectInfoResponse response = projectService.createProject(request);
        Project project = projectRepository.findById(response.projectId()).get();
        assertThat(project.getId()).isEqualTo(response.projectId());
    }

    @Nested
    class 프로젝트_조회 {
        @Test
        void 팀_ID로_조회하면_전체_프로젝트가_정상적으로_반환된다() {
            String inviteCode = teamService.createTeam("팀 이름", "팀 설명").inviteCode();
            Long teamId = teamService.getTeamByCode(inviteCode).teamId();

            ProjectCreateRequest request1 =
                    new ProjectCreateRequest(
                            teamId,
                            "project1",
                            "projectDescription",
                            "projectGoal",
                            startDt,
                            dueDt);
            ProjectCreateRequest request2 =
                    new ProjectCreateRequest(
                            teamId,
                            "project2",
                            "projectDescription",
                            "projectGoal",
                            startDt,
                            dueDt);

            ProjectCreateRequest request3 =
                    new ProjectCreateRequest(
                            teamId,
                            "project3",
                            "projectDescription",
                            "projectGoal",
                            startDt,
                            dueDt);

            ProjectCreateRequest request4 =
                    new ProjectCreateRequest(
                            teamId,
                            "project4",
                            "projectDescription",
                            "projectGoal",
                            startDt,
                            dueDt);

            ProjectInfoResponse response1 = projectService.createProject(request1);
            ProjectInfoResponse response2 = projectService.createProject(request2);
            // member logout 후 anotherMember 로그인
            logout();
            loginAs(anotherMember);
            // 팀 참가
            teamService.joinTeam(inviteCode);
            // anotherMember 새 프로젝트 생성
            ProjectInfoResponse response3 = projectService.createProject(request1);
            ProjectInfoResponse response4 = projectService.createProject(request2);

            ProjectListInfoResponse foundResponse = projectService.getProjectListInfo(teamId);
            assertThat(foundResponse.participatingProjects().contains(response3));
            assertThat(foundResponse.participatingProjects().contains(response4));
            assertThat(foundResponse.nonParticipatingProjects().contains(response1));
            assertThat(foundResponse.nonParticipatingProjects().contains(response2));
        }

        @Test
        void 프로젝트를_ID로_조회하면_정상적으로_반환된다() {

            Long teamId =
                    teamService
                            .getTeamByCode(teamService.createTeam("팀 이름", "팀 설명").inviteCode())
                            .teamId();
            ProjectCreateRequest request =
                    new ProjectCreateRequest(
                            teamId,
                            "projectTitle",
                            "projectDescription",
                            "projectGoal",
                            startDt,
                            dueDt);

            ProjectInfoResponse response = projectService.createProject(request);
            Project project = projectRepository.findById(response.projectId()).get();
            ProjectInfoResponse foundResponse = projectService.getProjectInfo(project.getId());
            assertThat(response).usingRecursiveComparison().isEqualTo(foundResponse);
        }

        @Test
        void ID가_유효하지_않으면_예외가_발생한다() {
            Long invalidProjectId = Long.MAX_VALUE;
            assertThatThrownBy(() -> projectService.getProjectInfo(invalidProjectId))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining("project 를 찾을 수 없습니다.");
        }
    }
}
