package com.amcamp.domain.task.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.AssignedStatus;
import com.amcamp.domain.task.domain.Task;
import com.amcamp.domain.task.dto.request.TaskCreateRequest;
import com.amcamp.domain.task.dto.request.TaskInfoUpdateRequest;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TaskErrorCode;
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
    private final SprintService sprintService;

    public void createTask(TaskCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(request.sprintId());
        final Project project = sprint.getProject();
        sprintService.validateProjectParticipant(project, project.getTeam(), currentMember);

        taskRepository.save(
                Task.createTask(
                        sprint,
                        request.description(),
                        request.startDt(),
                        request.dueDt(),
                        request.taskDifficulty()));
    }

    public TaskInfoResponse updateTaskInfo(Long taskId, TaskInfoUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        Task task = findByTaskId(taskId);

        validateTaskModify(currentMember, task);
        task.updateTask(request);
        return TaskInfoResponse.from(task);
    }

    public void deleteTask(Long taskId) {
        final Member currentMember = memberUtil.getCurrentMember();
        Task task = findByTaskId(taskId);
        validateTaskModify(currentMember, task);
        taskRepository.delete(task);
    }

    private void validateTaskModify(Member member, Task task) {
        if (task.getAssignedStatus() == AssignedStatus.ASSIGNED)
            if (!task.getAssignee()
                            .getProjectRole()
                            .equals(ProjectParticipantRole.ADMIN.getProjectRole())
                    || !member.equals(task.getAssignee().getTeamParticipant().getMember())) {
                throw new CommonException(TaskErrorCode.TASK_MODIFY_PERMISSION_REQUIRED);
            }
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
}
