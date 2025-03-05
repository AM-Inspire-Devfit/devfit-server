package com.amcamp.domain.sprint.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.sprint.dto.request.SprintBasicUpdateRequest;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.sprint.dto.request.SprintToDoUpdateRequest;
import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SprintServiceTest extends IntegrationTest {

    private final LocalDate startDt = LocalDate.of(2026, 1, 2);
    private final LocalDate dueDt = LocalDate.of(2026, 3, 1);

    @Autowired private SprintService sprintService;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;

    private Project project;

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

        Team team = Team.createTeam("testName", "testDescription");
        teamRepository.save(team);
        TeamParticipant teamParticipant =
                TeamParticipant.createParticipant(member, team, TeamParticipantRole.ADMIN);
        teamParticipantRepository.save(teamParticipant);

        project =
                Project.createProject(
                        team, "testTitle", "testDescription", "testGoal", startDt, dueDt);
        projectRepository.save(project);
        ProjectParticipant projectParticipant =
                ProjectParticipant.createProjectParticipant(
                        teamParticipant, project, ProjectParticipantRole.ADMIN);
        projectParticipantRepository.save(projectParticipant);
    }

    @Nested
    class 스프린트_생성할_때 {
        @Test
        void 프로젝트_기간_내라면_스프린트를_생성한다() {
            // given
            SprintCreateRequest request =
                    new SprintCreateRequest(project.getId(), "testGoal", startDt, dueDt);

            // when
            SprintInfoResponse response = sprintService.createSprint(request);

            // then
            assertThat(response.goal()).isEqualTo("testGoal");
            assertThat(response.startDt()).isEqualTo(LocalDate.of(2026, 1, 2));
            assertThat(response.dueDt()).isEqualTo(LocalDate.of(2026, 3, 1));
        }

        @Test
        void 프로젝트_기간_외라면_예외가_발생한다() {
            // given
            SprintCreateRequest request =
                    new SprintCreateRequest(
                            project.getId(),
                            "testGoal",
                            LocalDate.of(2026, 1, 1),
                            LocalDate.of(2026, 3, 2));

            // when & then
            assertThatThrownBy(() -> sprintService.createSprint(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(GlobalErrorCode.INVALID_DATE_ERROR.getMessage());
        }
    }

    @Nested
    class 스프린트_수정할_때 {
        @Test
        void 스프린트가_존재하지_않으면_예외가_발생한다() {
            // given
            SprintBasicUpdateRequest request = new SprintBasicUpdateRequest("testGoal");

            // when & then
            assertThatThrownBy(() -> sprintService.updateSprintBasicInfo(2L, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 스프린트_시작_날짜가_프로젝트_기간_내라면_성공한다() {
            // given
            sprintRepository.save(
                    Sprint.createSprint(project, "testTitle", "testDescription", startDt, dueDt));
            SprintToDoUpdateRequest request =
                    new SprintToDoUpdateRequest(
                            LocalDate.of(2026, 2, 14), LocalDate.of(2026, 2, 28), null);

            // when
            SprintInfoResponse response = sprintService.updateSprintToDoInfo(1L, request);

            // then
            assertThat(response.startDt()).isEqualTo(LocalDate.of(2026, 2, 14));
            assertThat(response.dueDt()).isEqualTo(LocalDate.of(2026, 2, 28));
            assertThat(response.status()).isEqualTo(ToDoStatus.NOT_STARTED);
            assertThat(response.title()).isEqualTo("testTitle");
        }
    }
}
