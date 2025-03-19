package com.amcamp.domain.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.member.application.MemberService;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.domain.ToDoStatus;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.task.application.TaskService;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.AssignedStatus;
import com.amcamp.domain.task.domain.SOSStatus;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.task.domain.TaskDifficulty;
import com.amcamp.domain.task.dto.request.TaskBasicInfoUpdateRequest;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TaskErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import com.amcamp.global.util.MemberUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
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
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 1),
                        "testProjectDescription");

        projectService.createProject(projectRequest);

        SprintCreateRequest sprintRequest =
                new SprintCreateRequest(
                        1L, "1차 스프린트", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));
        sprintService.createSprint(sprintRequest);
    }

    @Test
    void 태스크를_생성한다() {
        // given
        Member member = memberUtil.getCurrentMember();
        TaskCreateRequest taskRequest =
                new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);

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
    }

    @Nested
    class 태스크_수정_시 {
        //        @Test
        //        void 수정_권한이_없으면_예외처리() {
        //            // assignee가 존재할때, 프로젝트 팀장이 아니면 예외처리
        //            Member member = memberUtil.getCurrentMember();
        //            TaskCreateRequest taskRequest =
        //                    new TaskCreateRequest(
        //                            1L,
        //                            "피그마 화면 설계 수정",
        //                            TaskDifficulty.MID,
        //                            LocalDate.of(2026, 2, 1),
        //                            LocalDate.of(2026, 3, 1));
        //            taskService.createTask(taskRequest);
        //
        //            Member newMember = memberRepository.save(Member.createMember("member", null,
        // null));
        //            loginAs(newMember);
        //
        //            TeamInviteCodeResponse teamInviteCodeResponse = teamService.getInviteCode(1L);
        //            teamService.joinTeam(new
        // TeamInviteCodeRequest(teamInviteCodeResponse.inviteCode()));
        //
        //            // 프로젝트 참여 관련 코드 필요
        //
        //        }

        @Test
        void 정상적으로_기본_기한정보를_수정_한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest);

            // when - update basic info & assigned
            TaskBasicInfoUpdateRequest taskBasicInfoUpdateRequest =
                    new TaskBasicInfoUpdateRequest("피그마 화면 설계 재수정", TaskDifficulty.HIGH);

            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            TaskInfoResponse response =
                    taskService.updateTaskBasicInfo(1L, taskBasicInfoUpdateRequest);
            response = taskService.assignTask(1L);

            // then
            assertThat(task.getSprint().getId()).isEqualTo(taskRequest.sprintId());
            assertThat(response.description()).isEqualTo(taskBasicInfoUpdateRequest.description());
            assertThat(response.taskDifficulty())
                    .isEqualTo(taskBasicInfoUpdateRequest.taskDifficulty());
            assertThat(response.toDoStatus()).isEqualTo(ToDoStatus.ON_GOING);
            assertThat(response.assignedStatus()).isEqualTo(AssignedStatus.ASSIGNED);
            //			assertThat(response.projectNickname()).isEqualTo(member.getNickname());

            // when & then - finished
            response = taskService.updateTaskToDoInfo(1L);
            assertThat(response.toDoStatus()).isEqualTo(ToDoStatus.COMPLETED);

            // when - delete
            taskService.deleteTask(1L);

            // then
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

    @Nested
    class sos_실행_시 {
        @Test
        void 태스크가_할당되지않았을_경우_SOS_실행하지_않는다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest);

            // when
            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> taskService.updateTaskSOS(task.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(TaskErrorCode.TASK_NOT_ASSIGNED.getMessage());
        }

        @Test
        void 태스크가_할당되었을_경우_SOS_실행한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest);

            // when & then
            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            TaskInfoResponse response = taskService.assignTask(task.getId());
            assertThat(response.sosStatus()).isEqualTo(SOSStatus.NOT_SOS);

            response = taskService.updateTaskSOS(task.getId());
            assertThat(response.sosStatus()).isEqualTo(SOSStatus.SOS);
        }
        //		@Test
        //		void sos인_상태에서_태스크_할당상태를_수정한다 (){
        //			// given
        //			Member member = memberUtil.getCurrentMember();
        //			TaskCreateRequest taskRequest =
        //				new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
        //			taskService.createTask(taskRequest);
        //
        //			Task task =
        //				taskRepository
        //					.findById(1L)
        //					.orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));
        //
        //			taskService.assignTask(task.getId());
        //			taskService.updateTaskSOS(task.getId());
        //
        //			// 프로젝트 참가 메소드 필요
        //
        //		}

    }

    @Nested
    class 태스크_목록_조회_시 {
        @Test
        void 스프린트가_유효하지않으면_에러를_반환한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest1 =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest1);
            TaskCreateRequest taskRequest2 =
                    new TaskCreateRequest(1L, "mvp 완성", TaskDifficulty.HIGH);
            taskService.createTask(taskRequest2);

            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            taskService.assignTask(task.getId()); // 첫번쨰 태스크에만 담당자 배정

            // when & then
            //            assertThatThrownBy(() -> taskService.getTasksBySprint(2l))
            //                    .isInstanceOf(CommonException.class)
            //                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
            assertThatThrownBy(() -> taskService.getTasksBySprint(2l, 0L, 3))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 내가_담당하는_태스크_조회_시_프로젝트_참가자가_아니면_에러를_반환한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TeamInviteCodeResponse teamInviteCodeResponse = teamService.getInviteCode(1L);
            TaskCreateRequest taskRequest1 =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest1);
            TaskCreateRequest taskRequest2 =
                    new TaskCreateRequest(1L, "mvp 완성", TaskDifficulty.HIGH);
            taskService.createTask(taskRequest2);

            Member nonMember =
                    memberRepository.save(
                            Member.createMember("nonMember", "testProfileImageUrl", null));
            loginAs(nonMember);

            teamService.joinTeam(new TeamInviteCodeRequest(teamInviteCodeResponse.inviteCode()));

            // when & then
            assertThatThrownBy(() -> taskService.getTasksByMember(1l, 0L, 3))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트_태스크_조회_시_팀_참가자가_아니면_에러를_반환한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TeamInviteCodeResponse teamInviteCodeResponse = teamService.getInviteCode(1L);
            TaskCreateRequest taskRequest1 =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest1);
            TaskCreateRequest taskRequest2 =
                    new TaskCreateRequest(1L, "mvp 완성", TaskDifficulty.HIGH);
            taskService.createTask(taskRequest2);

            Member nonMember =
                    memberRepository.save(
                            Member.createMember("nonMember", "testProfileImageUrl", null));
            loginAs(nonMember);

            // when & then
            assertThatThrownBy(() -> taskService.getTasksBySprint(1l, 0L, 3))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED.getMessage());
        }

        @Test
        void 프로젝트별로_조회한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest1 =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest1);
            TaskCreateRequest taskRequest2 =
                    new TaskCreateRequest(1L, "mvp 완성", TaskDifficulty.HIGH);
            taskService.createTask(taskRequest2);

            // when
            Slice<TaskInfoResponse> result = taskService.getTasksBySprint(1L, 0L, 3);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).memberId()).isEqualTo(null);
            assertThat(result.getContent().get(0).projectNickname()).isEqualTo(null);
            assertThat(result.getContent().get(0).profileImageUrl()).isEqualTo(null);
        }

        @Test
        void 멤버별로_조회한다() {
            // given
            Member member = memberUtil.getCurrentMember();
            TaskCreateRequest taskRequest1 =
                    new TaskCreateRequest(1L, "피그마 화면 설계 수정", TaskDifficulty.MID);
            taskService.createTask(taskRequest1);
            TaskCreateRequest taskRequest2 =
                    new TaskCreateRequest(1L, "mvp 완성", TaskDifficulty.HIGH);
            taskService.createTask(taskRequest2);

            Task task =
                    taskRepository
                            .findById(1L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            taskService.assignTask(task.getId()); // 첫번쨰 태스크에만 담당자 배정

            Task task1 =
                    taskRepository
                            .findById(2L)
                            .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));

            taskService.assignTask(task1.getId()); // 첫번쨰 태스크에만 담당자 배정

            // when
            Slice<TaskInfoResponse> result = taskService.getTasksByMember(1L, 0L, 3);

            // then
            assertThat(result.getContent()).hasSize(2);
        }
    }
}
