package com.amcamp.domain.project.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.participant.dao.ParticipantRepository;
import com.amcamp.domain.participant.domain.Participant;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
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
    private final ParticipantRepository participantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;

    public ProjectInfoResponse getProjectInfo(Long projectId) {
        return ProjectInfoResponse.from(getProjectById(projectId));
    }

    public ProjectInfoResponse createProject(ProjectCreateRequest request) {
        // Team 불러오기
        Member member = memberUtil.getCurrentMember();
        Team team =
                teamRepository
                        .findById(request.TeamId())
                        .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

        // 팀에 속한 사용자 정보 가져오기
        Participant participant =
                participantRepository
                        .findByMemberAndTeam(member, team)
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                TeamErrorCode.TEAM_PARTICIPANT_NOT_FOUND));

        // 프로젝트 생성
        Project project =
                projectRepository.save(
                        Project.createProject(
                                team,
                                request.projectTitle(),
                                request.projectDescription(),
                                request.projectGoal(),
                                request.startDt(),
                                request.dueDt()));

        // 사용자를 프로젝트 참가자(프로젝트 관리자)로 등록
        projectParticipantRepository.save(
                ProjectParticipant.createProjectParticipant(
                        participant, project, ProjectParticipantRole.ADMIN));
        // 생성된 프로젝트 저장

        return ProjectInfoResponse.from(project);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }
}
