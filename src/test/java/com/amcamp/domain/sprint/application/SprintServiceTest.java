package com.amcamp.domain.sprint.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

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
import com.amcamp.domain.task.application.TaskService;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SprintServiceTest extends IntegrationTest {

    private final LocalDate dueDt = LocalDate.of(2026, 3, 1);

    @Autowired private SprintService sprintService;
    @Autowired private TaskService taskService;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;
    @Autowired private TaskRepository taskRepository;

    private Project project;

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

        Team team = Team.createTeam("testName", "testDescription");
        teamRepository.save(team);
        TeamParticipant teamParticipant =
                TeamParticipant.createParticipant(member, team, TeamParticipantRole.ADMIN);
        teamParticipantRepository.save(teamParticipant);

        project = Project.createProject(team, "testTitle", "testDescription", dueDt);
        projectRepository.save(project);
        ProjectParticipant projectParticipant =
                ProjectParticipant.createProjectParticipant(
                        teamParticipant,
                        project,
                        member.getNickname(),
                        member.getProfileImageUrl(),
                        ProjectParticipantRole.ADMIN);
        projectParticipantRepository.save(projectParticipant);
    }

    @Nested
    class 스프린트_생성할_때 {
        @Test
        void 프로젝트_기간_내라면_스프린트를_생성한다() {
            // given
            SprintCreateRequest request =
                    new SprintCreateRequest(project.getId(), "testGoal", dueDt);

            // when
            SprintInfoResponse response = sprintService.createSprint(request);

            // then
            assertThat(response.goal()).isEqualTo("testGoal");
            assertThat(response.dueDt()).isEqualTo(LocalDate.of(2026, 3, 1));
        }

        @Test
        void 프로젝트_기간_외라면_예외가_발생한다() {
            // given
            SprintCreateRequest request =
                    new SprintCreateRequest(project.getId(), "testGoal", LocalDate.of(2026, 3, 2));

            // when & then
            assertThatThrownBy(() -> sprintService.createSprint(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SprintErrorCode.SPRINT_DUE_DATE_INVALID.getMessage());
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
                    Sprint.createSprint(project, "testTitle", "testDescription", dueDt));
            SprintToDoUpdateRequest request =
                    new SprintToDoUpdateRequest(LocalDate.of(2026, 2, 28), null);

            // when
            SprintInfoResponse response = sprintService.updateSprintToDoInfo(1L, request);

            // then
            assertThat(response.dueDt()).isEqualTo(LocalDate.of(2026, 2, 28));
            assertThat(response.status()).isEqualTo(ToDoStatus.ON_GOING);
            assertThat(response.title()).isEqualTo("testTitle");
        }
    }

    @Nested
    class 스프린트_삭제할_때 {
        @Test
        void 스프린트가_존재하지_않으면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> sprintService.deleteSprint(2L))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 프로젝트_리더가_삭제할_경우_성공한다() {
            // given
            sprintRepository.save(
                    Sprint.createSprint(project, "testTitle", "testDescription", dueDt));

            // when
            sprintService.deleteSprint(1L);

            // then
            assertThat(sprintRepository.findAll()).isEmpty();
            assertThat(sprintRepository.count()).isEqualTo(0);
        }
    }

    @Nested
    class 프로젝트별_스프린트_목록을_조회할_때 {
        @Test
        void 프로젝트가_존재하지_않으면_예외가_발생한다() {
            // when & then
            assertThatThrownBy(() -> sprintService.findAllSprint(999L, null))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(ProjectErrorCode.PROJECT_NOT_FOUND.getMessage());
        }

        @Test
        void 프로젝트가_존재한다면_첫_번쨰_스프린트를_반환한다() {
            // given
            List<Sprint> sprintList =
                    List.of(
                            Sprint.createSprint(project, "1", "testDescription1", dueDt),
                            Sprint.createSprint(project, "2", "testDescription2", dueDt),
                            Sprint.createSprint(project, "3", "testDescription3", dueDt));
            sprintRepository.saveAll(sprintList);

            // when
            Slice<SprintInfoResponse> results = sprintService.findAllSprint(project.getId(), null);

            // then
            assertThat(results.getSize()).isEqualTo(1);
            assertThat(results)
                    .extracting("id", "title", "goal")
                    .containsExactlyInAnyOrder(tuple(1L, "1", "testDescription1"));
        }
    }
}
