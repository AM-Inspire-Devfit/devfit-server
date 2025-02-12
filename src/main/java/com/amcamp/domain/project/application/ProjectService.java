package com.amcamp.domain.project.application;

import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    public ProjectInfoResponse getProjectInfo(Long projectId) {
        return ProjectInfoResponse.from(getProjectById(projectId));
    }

    public ProjectInfoResponse createProject(ProjectCreateRequest request) {
        // Team 불러오기
        Team team =
                teamRepository
                        .findById(request.TeamId())
                        .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
        // 프로젝트 생성
        Project project =
                Project.createProject(
                        team,
                        request.projectTitle(),
                        request.projectDescription(),
                        request.projectGoal(),
                        request.startDt(),
                        request.dueDt());
        // 생성된 프로젝트 저장
        projectRepository.save(project);
        return ProjectInfoResponse.from(project);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }
}
