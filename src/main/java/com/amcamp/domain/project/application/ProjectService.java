package com.amcamp.domain.project.application;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRegistrationRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.dao.ProjectRepositoryCustom;
import com.amcamp.domain.project.domain.*;
import com.amcamp.domain.project.domain.ProjectRegistration;
import com.amcamp.domain.project.dto.request.*;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectListInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectParticipantInfoResponse;
import com.amcamp.domain.project.dto.response.ProjectRegistrationInfoResponse;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
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
    private final MemberRepository memberRepository;
    private final ProjectRegistrationRepository projectRegistrationRepository;

    public void createProject(ProjectCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Team team = getTeam(request.teamId());
        TeamParticipant teamParticipant = getValidTeamParticipant(member, team);
        Project project =
                projectRepository.save(
                        Project.createProject(
                                team,
                                normalizeProjectTitle(request.projectTitle()),
                                request.projectDescription(),
                                request.startDt(),
                                request.dueDt()));

        projectParticipantRepository.save(
                ProjectParticipant.createProjectParticipant(
                        teamParticipant,
                        project,
                        member.getNickname(),
                        member.getProfileImageUrl(),
                        ProjectParticipantRole.ADMIN));
    }

    @Transactional(readOnly = true)
    public ProjectInfoResponse getProjectInfo(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getValidTeamParticipant(member, project.getTeam());
        return ProjectInfoResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectListInfoResponse> getProjectListInfo(Long teamId) {
        List<Project> projectList = projectRepositoryCustom.findAllByTeamId(teamId);
        Member member = memberUtil.getCurrentMember();
        TeamParticipant teamParticipant = getValidTeamParticipant(member, getTeam(teamId));
        return projectList.stream()
                .map(
                        p ->
                                new ProjectListInfoResponse(
                                        ProjectInfoResponse.from(p),
                                        isProjectParticipant(p, teamParticipant)))
                .collect(Collectors.toList());
    }

    // update
    public void updateProjectBasicInfo(Long projectId, ProjectBasicInfoUpdateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getValidProjectParticipant(member, project);
        project.updateBasic(request.title(), request.description());
    }

    public void updateProjectTodoInfo(Long projectId, ProjectTodoInfoUpdateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getValidProjectParticipant(member, project);
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
        ProjectParticipant participant = getValidProjectParticipant(member, project);
        if (isProjectAdmin(member, project)) {
            boolean hasOtherParticipants =
                    projectParticipantRepository.existsByProjectAndProjectRoleNot(
                            project, ProjectParticipantRole.ADMIN);
            // 다른 팀원 있을 떄 -> 예외 발생
            if (hasOtherParticipants) {
                throw new CommonException(ProjectErrorCode.PROJECT_ADMIN_CANNOT_LEAVE);
            }
            projectParticipantRepository.deleteAllByProject(project);
            projectRepository.delete(project);
        } else {
            projectParticipantRepository.delete(participant);
        }
    }

    public void changeProjectAdmin(Long projectId, Long newAdminId) {
        Member currentMember = memberUtil.getCurrentMember(); // 현재 사용자 (기존 Admin)
        Project project = getProjectById(projectId);
        ProjectParticipant currentAdmin = validateProjectAdmin(currentMember, project);

        Member newAdminMember =
                memberRepository
                        .findById(newAdminId)
                        .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));
        ProjectParticipant newAdmin = getValidProjectParticipant(newAdminMember, project);

        currentAdmin.changeRole(ProjectParticipantRole.MEMBER);
        newAdmin.changeRole(ProjectParticipantRole.ADMIN);
    }

    // project Registration
    public void requestToProjectRegistration(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        TeamParticipant requester = validateProjectRegistrationAlreadyExists(member, project);
        projectRegistrationRepository.save(ProjectRegistration.createRequest(project, requester));
    }

    public ProjectRegistrationInfoResponse getProjectRegistration(
            Long projectId, Long registrationId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        validateProjectAdmin(member, project);
        return ProjectRegistrationInfoResponse.from(getProjectRegistrationById(registrationId));
    }

    public List<ProjectRegistrationInfoResponse> getProjectRegistrationList(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        validateProjectAdmin(member, project);
        return projectRegistrationRepository.findAllByProject(project).stream()
                .map(ProjectRegistrationInfoResponse::from)
                .collect(Collectors.toList());
    }

    public void approveProjectRegistration(Long projectId, Long registrationId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        validateProjectAdmin(member, project);
        ProjectRegistration registration = getProjectRegistrationById(registrationId);
        projectParticipantRepository.save(
                ProjectParticipant.createProjectParticipant(
                        registration.getRequester(),
                        project,
                        registration.getRequester().getMember().getNickname(),
                        registration.getRequester().getMember().getProfileImageUrl(),
                        ProjectParticipantRole.MEMBER));
        registration.updateStatus(ProjectRegistrationStatus.APPROVED);
    }

    public void rejectProjectRegistration(Long projectId, Long registrationId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        validateProjectAdmin(member, project);
        getProjectRegistrationById(registrationId).updateStatus(ProjectRegistrationStatus.REJECTED);
    }

    public void deleteProjectRegistration(Long projectId, Long projectRegisterId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        TeamParticipant teamParticipant = getValidTeamParticipant(member, project.getTeam());
        ProjectRegistration registration = getProjectRegistrationById(projectRegisterId);

        if (teamParticipant.equals(registration.getRequester())) {
            projectRegistrationRepository.delete(registration);
        } else {
            throw new CommonException(ProjectErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    // project participant info
    public ProjectParticipantInfoResponse getProjectParticipant(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        ProjectParticipant participant = getValidProjectParticipant(member, project);
        return ProjectParticipantInfoResponse.from(participant);
    }

    public List<ProjectParticipantInfoResponse> getProjectParticipantList(Long projectId) {
        Member member = memberUtil.getCurrentMember();
        Project project = getProjectById(projectId);
        getValidProjectParticipant(member, project);
        return projectParticipantRepository.findAllByProject(project).stream()
                .map(ProjectParticipantInfoResponse::from)
                .collect(Collectors.toList());
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

    private TeamParticipant getValidTeamParticipant(Member member, Team team) {
        return teamParticipantRepository
                .findByMemberAndTeam(member, team)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));
    }

    private ProjectParticipant getValidProjectParticipant(Member member, Project project) {
        TeamParticipant teamParticipant = getValidTeamParticipant(member, project.getTeam());
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

    private TeamParticipant validateProjectRegistrationAlreadyExists(
            Member member, Project project) {
        TeamParticipant participant = getValidTeamParticipant(member, project.getTeam());
        if (projectRegistrationRepository.findByRequester(participant).isPresent()) {
            throw new CommonException(ProjectErrorCode.PROJECT_REGISTRATION_ALREADY_EXISTS);
        } else {
            return participant;
        }
    }

    private ProjectRegistration getProjectRegistrationById(Long registrationId) {
        return projectRegistrationRepository
                .findById(registrationId)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_REGISTRATION_NOT_FOUND));
    }

    private ProjectParticipant validateProjectAdmin(Member member, Project project) {
        ProjectParticipant participant = getValidProjectParticipant(member, project);
        if (!participant.getProjectRole().equals(ProjectParticipantRole.ADMIN)) {
            throw new CommonException(ProjectErrorCode.UNAUTHORIZED_ACCESS);
        }
        return participant;
    }

    private boolean isProjectAdmin(Member member, Project project) {
        ProjectParticipant participant = getValidProjectParticipant(member, project);
        return participant.getProjectRole().equals(ProjectParticipantRole.ADMIN);
    }

    private Boolean isProjectParticipant(Project project, TeamParticipant teamParticipant) {
        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .isPresent();
    }
}
