package com.amcamp.domain.sprint.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.*;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.sprint.dto.request.SprintBasicUpdateRequest;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.sprint.dto.request.SprintToDoUpdateRequest;
import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import com.amcamp.domain.sprint.dto.response.SprintProgressResponse;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class SprintService {

    private final MemberUtil memberUtil;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TeamParticipantRepository teamParticipantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final TaskRepository taskRepository;

    public SprintInfoResponse createSprint(SprintCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Project project = findByProjectId(request.projectId());

        validateProjectParticipant(project, project.getTeam(), currentMember);
        validateDate(request.startDt(), request.dueDt(), project.getToDoInfo());

        long count = sprintRepository.countByProject(project);
        String autoTitle = String.valueOf(count + 1);

        Sprint sprint =
                sprintRepository.save(
                        Sprint.createSprint(
                                project,
                                autoTitle,
                                request.goal(),
                                request.startDt(),
                                request.dueDt()));

        return SprintInfoResponse.from(sprint);
    }

    public SprintInfoResponse updateSprintBasicInfo(
            Long sprintId, SprintBasicUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);

        validateProjectParticipant(
                sprint.getProject(), sprint.getProject().getTeam(), currentMember);

        sprint.updateSprintBasic(request.goal());

        return SprintInfoResponse.from(sprint);
    }

    public SprintInfoResponse updateSprintToDoInfo(Long sprintId, SprintToDoUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);

        validateProjectParticipant(
                sprint.getProject(), sprint.getProject().getTeam(), currentMember);

        validateDate(request.startDt(), request.dueDt(), sprint.getProject().getToDoInfo());

        sprint.updateSprintToDo(request.startDt(), request.dueDt(), request.status());

        return SprintInfoResponse.from(sprint);
    }

    public void deleteSprint(Long sprintId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);

        ProjectParticipant projectParticipant =
                validateProjectParticipant(
                        sprint.getProject(), sprint.getProject().getTeam(), currentMember);

        validateAdminProjectParticipant(projectParticipant);

        sprintRepository.deleteById(sprintId);

        List<Sprint> sprintList =
                sprintRepository.findAllByProjectOrderByCreatedAt(sprint.getProject());
        for (int i = 0; i < sprintList.size(); i++) {
            sprintList.get(i).updateSprintTitle(String.valueOf(i + 1));
        }
    }

    @Transactional(readOnly = true)
    public Slice<SprintInfoResponse> findAllSprint(Long projectId, Long lastSprintId) {
		final Member currentMember = memberUtil.getCurrentMember();
		final Project project = findByProjectId(projectId);

		teamParticipantRepository
			.findByMemberAndTeam(currentMember, project.getTeam())
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));

		return sprintRepository.findAllSprintByProjectId(projectId, lastSprintId);
	}
    public SprintProgressResponse getSprintProgress(Long sprintId) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);
        validateTeamParticipant(sprint.getProject().getTeam(), currentMember);

        int totalTasks =
                Optional.ofNullable(taskRepository.countBySprint(sprint))
                        .filter(count -> count != 0)
                        .orElseThrow(
                                () -> new CommonException(SprintErrorCode.TASK_NOT_CREATED_YET));
        int completedTasks =
                taskRepository.countBySprintAndTodoStatus(sprint, ToDoStatus.COMPLETED);

        Double progress = (double) (completedTasks * 100 / totalTasks);
        sprint.updateProgress(progress);

        return SprintProgressResponse.from(sprintId, progress);
    }

    private void validateTeamParticipant(Team team, Member currentMember) {
        TeamParticipant teamParticipant =
                teamParticipantRepository
                        .findByMemberAndTeam(currentMember, team)
                        .orElseThrow(
                                () -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));
    }

    private Sprint findBySprintId(Long sprintId) {
        return sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new CommonException(SprintErrorCode.SPRINT_NOT_FOUND));
    }

    private Project findByProjectId(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND));
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

    private void validateAdminProjectParticipant(ProjectParticipant projectParticipant) {
        if (!projectParticipant.getProjectRole().equals(ProjectParticipantRole.ADMIN)) {
            throw new CommonException(SprintErrorCode.SPRINT_DELETE_FORBIDDEN);
        }
    }

    private void validateDate(LocalDate startDt, LocalDate dueDt, ToDoInfo toDoInfo) {
        if (startDt.isBefore(toDoInfo.getStartDt()) || dueDt.isAfter(toDoInfo.getDueDt())) {
            throw new CommonException(GlobalErrorCode.INVALID_DATE_ERROR);
        }
    }
}
