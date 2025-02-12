package com.amcamp.domain.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @Autowired private ProjectParticipantRepository projectParticipantRepository;
    @Autowired private ProjectRepository projectRepository;

    private ProjectCreateRequest request;
    private Member member;

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @BeforeEach
    private void setUp() {
        member =
                Member.createMember(
                        "testNickName",
                        "testProfileImageUrl",
                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        memberRepository.save(member);
        loginAs(member);
        Long teamId =
                teamService
                        .getTeamByCode(teamService.createTeam("팀 이름", "팀 설명").inviteCode())
                        .teamId();
        request =
                new ProjectCreateRequest(
                        teamId,
                        "projectTitle",
                        "projectDescription",
                        "projectGoal",
                        LocalDateTime.of(2025, 1, 1, 1, 00),
                        LocalDateTime.of(2025, 1, 1, 1, 00));
    }

    @Test
    void 프로젝트를_생성하면_정상적으로_저장된다() {
        ProjectInfoResponse response = projectService.createProject(request);
        Project project = projectRepository.findById(response.projectId()).get();
        assertThat(project.getId()).isEqualTo(response.projectId());
    }

    @Nested
    class 프로젝트_조회 {
        @Test
        void 프로젝트를_ID로_조회하면_정상적으로_반환된다() {
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
