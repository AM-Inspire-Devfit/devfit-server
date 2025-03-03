package com.amcamp.domain.sprint.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.sprint.dto.request.SprintBasicUpdateRequest;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.sprint.dto.request.SprintToDoUpdateRequest;
import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
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

    public void createSprint(SprintCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Project project = findByProjectId(request.projectId());

        validateProjectParticipant(project, project.getTeam(), currentMember);

        sprintRepository.save(
                Sprint.createSprint(
                        project,
                        request.title(),
                        request.goal(),
                        request.startDt(),
                        request.dueDt()));
    }

    public SprintInfoResponse updateSprintBasicInfo(
            Long sprintId, SprintBasicUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);

        validateProjectParticipant(
                sprint.getProject(), sprint.getProject().getTeam(), currentMember);

        sprint.updateSprintBasic(request.title(), request.goal());

        return SprintInfoResponse.from(sprint);
    }

    public SprintInfoResponse updateSprintToDoInfo(Long sprintId, SprintToDoUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final Sprint sprint = findBySprintId(sprintId);

        validateProjectParticipant(
                sprint.getProject(), sprint.getProject().getTeam(), currentMember);

        sprint.updateSprintToDo(request.startDt(), request.dueDt(), request.status());

        return SprintInfoResponse.from(sprint);
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

    private void validateProjectParticipant(Project project, Team team, Member currentMember) {
        TeamParticipant teamParticipant =
                teamParticipantRepository
                        .findByMemberAndTeam(currentMember, team)
                        .orElseThrow(
                                () -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));

        projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED));
    }
}
