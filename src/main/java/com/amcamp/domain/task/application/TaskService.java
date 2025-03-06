package com.amcamp.domain.task.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.AssignedStatus;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.task.dto.request.TaskBasicInfoUpdateRequest;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.*;
import com.amcamp.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TaskService {
    private final MemberUtil memberUtil;
    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final TeamParticipantRepository teamParticipantRepository;

    public TaskInfoResponse createTask(TaskCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(request.sprintId());
        final Project project = sprint.getProject();
        validateProjectParticipant(project, project.getTeam(), currentMember);
        Task task =
                taskRepository.save(
                        Task.createTask(sprint, request.description(), request.taskDifficulty()));

        return findProjectParticipantMember(task) != null
                ? TaskInfoResponse.from(task, findProjectParticipantMember(task))
                : TaskInfoResponse.from(task);
    }

    public TaskInfoResponse updateTaskBasicInfo(Long taskId, TaskBasicInfoUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Task task = findByTaskId(taskId);
        final Sprint sprint = findBySprintId(task.getSprint().getId());
        final Project project = sprint.getProject();

        validateProjectParticipant(project, project.getTeam(), currentMember);
        validateTaskModify(currentMember, task);
        task.updateTaskBasicInfo(request);

        return findProjectParticipantMember(task) != null
                ? TaskInfoResponse.from(task, findProjectParticipantMember(task))
                : TaskInfoResponse.from(task);
    }

    public TaskInfoResponse updateTaskToDoInfo(Long taskId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Task task = findByTaskId(taskId);
        final Sprint sprint = findBySprintId(task.getSprint().getId());
        final Project project = sprint.getProject();

        validateProjectParticipant(project, project.getTeam(), currentMember);
        validateTaskModify(currentMember, task);
        task.updateTaskTodoInfo();

        return findProjectParticipantMember(task) != null
                ? TaskInfoResponse.from(task, findProjectParticipantMember(task))
                : TaskInfoResponse.from(task);
    }

    public TaskInfoResponse updateTaskSOS(Long taskId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Task task = findByTaskId(taskId);
        final Sprint sprint = findBySprintId(task.getSprint().getId());
        final Project project = sprint.getProject();

        validateProjectParticipant(project, project.getTeam(), currentMember);
        validateTaskModify(currentMember, task);
        task.updateTaskSOS();

        return findProjectParticipantMember(task) != null
                ? TaskInfoResponse.from(task, findProjectParticipantMember(task))
                : TaskInfoResponse.from(task);
    }

    public TaskInfoResponse assignTask(Long taskId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Task task = findByTaskId(taskId);
        final Sprint sprint = findBySprintId(task.getSprint().getId());
        final Project project = sprint.getProject();

        ProjectParticipant projectParticipant =
                validateProjectParticipant(project, project.getTeam(), currentMember);

        if (task.getAssignedStatus() != AssignedStatus.NOT_ASSIGNED && task.getAssignee() != null) {
            throw new CommonException(TaskErrorCode.TASK_ALREADY_ASSIGNED);
        }

        task.assignTask(projectParticipant);
        return findProjectParticipantMember(task) != null
                ? TaskInfoResponse.from(task, findProjectParticipantMember(task))
                : TaskInfoResponse.from(task);
    }

    public void deleteTask(Long taskId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Task task = findByTaskId(taskId);
        final Sprint sprint = findBySprintId(task.getSprint().getId());
        final Project project = sprint.getProject();

        validateProjectParticipant(project, project.getTeam(), currentMember);
        validateTaskModify(currentMember, task);
        taskRepository.delete(task);
    }

    private void validateTaskModify(Member member, Task task) {
        if (task.getAssignedStatus() != AssignedStatus.NOT_ASSIGNED || task.getAssignee() != null) {
            if (!task.getAssignee().getProjectRole().equals(ProjectParticipantRole.ADMIN)
                    || !member.equals(task.getAssignee().getTeamParticipant().getMember())) {
                throw new CommonException(TaskErrorCode.TASK_MODIFY_FORBIDDEN);
            }
        }
    }

    private ProjectParticipant validateProjectParticipant(
            Project project, Team team, Member currentMember) {
        TeamParticipant teamParticipant =
                teamParticipantRepository
                        .findByMemberAndTeam(currentMember, team)
                        .orElseThrow(
                                () -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));

        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED));
    }

    private Sprint findBySprintId(Long sprintId) {
        return sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new CommonException(SprintErrorCode.SPRINT_NOT_FOUND));
    }

    private Task findByTaskId(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new CommonException(TaskErrorCode.TASK_NOT_FOUND));
    }

    private Member findProjectParticipantMember(Task task) {
        Member member = null;
        if (task.getAssignee() != null && task.getAssignedStatus() != AssignedStatus.NOT_ASSIGNED) {
            member = task.getAssignee().getTeamParticipant().getMember();
        }
        return member;
    }
}
