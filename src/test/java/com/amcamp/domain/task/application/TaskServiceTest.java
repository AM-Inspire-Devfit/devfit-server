package com.amcamp.domain.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.member.application.MemberService;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.task.domain.TaskDifficulty;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.task.dto.request.TaskInfoUpdateRequest;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TaskErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import com.amcamp.global.util.MemberUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TaskServiceTest extends IntegrationTest {
    @Autowired private MemberService memberService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberUtil memberUtil;
    @Autowired private ProjectService projectService;
    @Autowired private SprintService sprintService;
    @Autowired private TaskService taskService;
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
                        1L,
                        "1차 스프린트",
                        "MVP 개발",
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 3, 1));
        sprintService.createSprint(sprintRequest);
    }

    @Test
    void 태스크를_생성한다() {
        // given
        Member member = memberUtil.getCurrentMember();
        TaskCreateRequest taskRequest =
                new TaskCreateRequest(
                        1L,
                        "피그마 화면 설계 수정",
                        TaskDifficulty.MID,
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 3, 1));

        // when
        taskService.createTask(taskRequest);

        // then
        Task task =
                taskRepository
                        .findById(1L)
                        .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

        assertThat(task.getSprint().getId()).isEqualTo(taskRequest.sprintId());
        assertThat(task.getDescription()).isEqualTo(taskRequest.description());
        assertThat(task.getTaskDifficulty()).isEqualTo(taskRequest.taskDifficulty());
        assertThat(task.getToDoInfo().getStartDt()).isEqualTo(taskRequest.startDt());
        assertThat(task.getToDoInfo().getDueDt()).isEqualTo(taskRequest.dueDt());
    }

    @Nested
    class 태스크_수정_삭제시 {
        @Test
        void 수정_삭제_권한이_없으면_예외처리() {
            // assignee가 존재할때, 프로젝트 팀장이 아니면 예외처리
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest =
                    new TaskCreateRequest(
                            1L,
                            "피그마 화면 설계 수정",
                            TaskDifficulty.MID,
                            LocalDate.of(2026, 2, 1),
                            LocalDate.of(2026, 3, 1));
            taskService.createTask(taskRequest);

            Member newMember = memberRepository.save(Member.createMember("member", null, null));
            loginAs(newMember);

            TeamInviteCodeResponse teamInviteCodeResponse = teamService.getInviteCode(1L);
            teamService.joinTeam(new TeamInviteCodeRequest(teamInviteCodeResponse.inviteCode()));

            // 프로젝트 참여 관련 코드 필요

        }

        @Test
        void 정상적으로_수정_삭제한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest =
                    new TaskCreateRequest(
                            1L,
                            "피그마 화면 설계 수정",
                            TaskDifficulty.MID,
                            LocalDate.of(2026, 2, 1),
                            LocalDate.of(2026, 3, 1));
            taskService.createTask(taskRequest);

            // when
            TaskInfoUpdateRequest taskInfoUpdateRequest =
                    new TaskInfoUpdateRequest(
                            "피그마 화면 설계 재수정",
                            TaskDifficulty.HIGH,
                            LocalDate.of(2026, 2, 1),
                            LocalDate.of(2026, 3, 1));

            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            taskService.updateTaskInfo(1L, taskInfoUpdateRequest);

            // then

            assertThat(task.getSprint().getId()).isEqualTo(taskRequest.sprintId());
            assertThat(task.getDescription()).isEqualTo(taskInfoUpdateRequest.description());
            assertThat(task.getTaskDifficulty()).isEqualTo(taskInfoUpdateRequest.taskDifficulty());
            assertThat(task.getToDoInfo().getStartDt()).isEqualTo(taskInfoUpdateRequest.startDt());
            assertThat(task.getToDoInfo().getDueDt()).isEqualTo(taskInfoUpdateRequest.dueDt());

            taskService.deleteTask(1L); // 태스크 삭제

            // 삭제 후 태스크를 다시 조회할 때 예외가 발생하는지 확인
            assertThatThrownBy(
                            () ->
                                    taskRepository
                                            .findById(1L)
                                            .orElseThrow(
                                                    () ->
                                                            new CommonException(
                                                                    TaskErrorCode.TASK_NOT_FOUND)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(TaskErrorCode.TASK_NOT_FOUND.getMessage());
        }
    }
}
