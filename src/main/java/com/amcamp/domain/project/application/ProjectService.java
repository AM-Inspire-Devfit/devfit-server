package com.amcamp.domain.project.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.dao.ProjectRepositoryCustom;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.project.dto.request.*;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectParticipationInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final MemberUtil memberUtil;
    private final TeamParticipantRepository teamParticipantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectRepositoryCustom projectRepositoryCustom;

    public void createProject(ProjectCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Team team = getTeam(request.teamId());
        TeamParticipant teamParticipant = getTeamParticipant(member, team);
        Project project =
                projectRepository.save(
                        Project.createProject(
                                team,
                                normalizeProjectTitle(request.projectTitle()),
                                request.projectDescription(),
                                request.projectGoal(),
                                request.startDt(),
                                request.dueDt()));

        projectParticipantRepository.save(
                ProjectParticipant.createProjectParticipant(
                        teamParticipant, project, ProjectParticipantRole.ADMIN));
    }

    @Transactional(readOnly = true)
    public ProjectInfoResponse getProjectInfo(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getProjectParticipant(member, project);
        return ProjectInfoResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectParticipationInfoResponse> getProjectListInfo(Long teamId) {
        List<Project> projectList = projectRepositoryCustom.findAllByTeamId(teamId);
        Member member = memberUtil.getCurrentMember();
        TeamParticipant teamParticipant = getTeamParticipant(member, getTeam(teamId));
        return projectList.stream()
                .map(
                        p ->
                                new ProjectParticipationInfoResponse(
                                        ProjectInfoResponse.from(p),
                                        isProjectParticipant(p, teamParticipant)))
                .collect(Collectors.toList());
    }

    // update
    public void updateProjectBasicInfo(Long projectId, ProjectBasicInfoUpdateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getProjectParticipant(member, project);
        project.updateBasic(request.title(), request.goal(), request.description());
    }

    public void updateProjectTodoInfo(Long projectId, ProjectTodoInfoUpdateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getProjectParticipant(member, project);
        project.getToDoInfo()
                .updateToDoInfo(request.startDt(), request.DueDt(), request.toDoStatus());
    }

    // delete

    public void deleteProject(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        validateProjectAdmin(member, project);
        projectParticipantRepository.deleteAllByProject(project);
        projectRepository.delete(project);
    }

    public void deleteProjectParticipant(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        projectParticipantRepository.delete(getProjectParticipant(member, project));
    }

    // project utils

    private String normalizeProjectTitle(String name) {
        return name.trim().replaceAll("[^0-9a-zA-Z가-힣 ]", "_");
    }

    private Project getProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    private TeamParticipant getTeamParticipant(Member member, Team team) {
        return teamParticipantRepository
                .findByMemberAndTeam(member, team)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));
    }

    private ProjectParticipant getProjectParticipant(Member member, Project project) {
        TeamParticipant teamParticipant = getTeamParticipant(member, project.getTeam());
        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED));
    }

    private Team getTeam(Long teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
    }

    private void validateProjectAdmin(Member member, Project project) {
        ProjectParticipant participant = getProjectParticipant(member, project);
        if (!participant.getProjectRole().equals(ProjectParticipantRole.ADMIN)) {
            throw new CommonException(ProjectErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private Boolean isProjectParticipant(Project project, TeamParticipant teamParticipant) {
        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .isPresent();
    }
}
