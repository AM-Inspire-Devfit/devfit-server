package com.amcamp.domain.project.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.dao.ProjectRepositoryCustom;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectListInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import java.util.List;
import java.util.Map;
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
                                request.projectTitle(),
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
        return ProjectInfoResponse.from(getProjectById(projectId));
    }

    @Transactional(readOnly = true)
    public ProjectListInfoResponse getProjectListInfo(Long teamId) {
        List<Project> projectList = projectRepositoryCustom.findAllByTeamId(teamId);
        Member member = memberUtil.getCurrentMember();
        Team team = getTeam(teamId);

        TeamParticipant teamParticipant = getTeamParticipant(member, team);

        Map<Boolean, List<ProjectInfoResponse>> partitionedProjects =
                projectList.stream()
                        .collect(
                                Collectors.partitioningBy(
                                        project -> isTeamParticipant(project, teamParticipant),
                                        Collectors.mapping(
                                                ProjectInfoResponse::from, Collectors.toList())));

        return new ProjectListInfoResponse(
                partitionedProjects.get(true), partitionedProjects.get(false));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    // util methods
    private TeamParticipant getTeamParticipant(Member member, Team team) {
        return teamParticipantRepository
                .findByMemberAndTeam(member, team)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));
    }

    private Team getTeam(Long teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
    }

    private Boolean isTeamParticipant(Project project, TeamParticipant teamParticipant) {
        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .isPresent();
    }
}
